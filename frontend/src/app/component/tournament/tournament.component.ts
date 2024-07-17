import { Component } from '@angular/core';
import {TournamentDetailDto, TournamentListDto, TournamentSearchParams} from "../../dto/tournament";
import {TournamentService} from "../../service/tournament.service";
import {ToastrService} from "ngx-toastr";
import {debounceTime, Subject} from "rxjs";

@Component({
  selector: 'app-tournament',
  templateUrl: './tournament.component.html',
  styleUrls: ['./tournament.component.scss']
})
export class TournamentComponent {
  tournaments: TournamentListDto[] = [];
  searchParams: TournamentSearchParams = {};
  searchStartDate: string | null = null;
  searchFinishDate: string | null = null;
  bannerError: string | null = null;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }

  reloadTournaments() {
    if (this.searchStartDate == null || this.searchStartDate === "") {
      delete this.searchParams.startDate;
    } else {
      this.searchParams.startDate = new Date(this.searchStartDate);
    }
    if (this.searchFinishDate == null || this.searchFinishDate === "") {
      delete this.searchParams.endDate;
    } else {
      this.searchParams.endDate = new Date(this.searchFinishDate);
    }

    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.tournaments = data;
        },
        error: err => {
          console.error('Error fetching tournaments', err);
          this.bannerError = 'Could not fetch tournaments: ' + err.message;
          const errMessage = err.status === 0
            ? 'Is the backend up?'
            : err.message.message;
          this.notification.error(errMessage, 'Could Not Fetch Tournaments');
        }
      });
  }

  searchChanged(): void {
    this.searchChangedObservable.next();
  }
}
