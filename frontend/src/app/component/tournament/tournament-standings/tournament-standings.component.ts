import {Component, OnInit} from '@angular/core';
import {
  TournamentStandingsDto,
  TournamentStandingsTreeDto
} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | undefined;
  id: string | null = '';

  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  public ngOnInit() {
    return this.route.paramMap.subscribe({
      next: params => {
        this.id = params.get('id');
        this.service.getTournamentStandings(this.id).subscribe({
            next: data => {
              this.standings = data;
            },
          }
        );
      }
    });
  }

  public submit(form: NgForm) {
    this.ngOnInit()
    return this.route.paramMap.subscribe({
      next: params => {
        this.service.update(this.id, this.standings).subscribe({
            next: data => {
              if (this.standings)
                this.standings = data;
            },
            error: err => {
              console.error(err.message, err);
              this.notification.error(this.errorFormatter.format(err), "Could Not Update Tournaments", {
                enableHtml: true,
                timeOut: 10000,
              });
            }
          }
        );
      }
    });
  }

  public generateFirstRound() {
    if (!this.standings)
      return;
    return this.route.paramMap.subscribe({
      next: params => {
        this.service.generateFirstRound(this.id, this.standings).subscribe({
            next: data => {
              if (this.standings)
                this.standings = data;
            },
            error: err => {
              console.error(err.message, err);
              this.notification.error(this.errorFormatter.format(err), "Could Not Generate First Round", {
                enableHtml: true,
                timeOut: 10000,
              });
            }
          }
        );
      }
    });
  }
}
