import {Component} from '@angular/core';
import {Horse} from "../../../dto/horse";
import {HorseService} from "../../../service/horse.service";
import {BreedService} from "../../../service/breed.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Observable} from "rxjs";
import {Breed} from "../../../dto/breed";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-horse-details',
  templateUrl: './horse-details.component.html',
  styleUrls: ['./horse-details.component.scss']
})
export class HorseDetailsComponent {

  horse: Horse | undefined;

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
  ) {
  }

  ngOnInit(): void {
    this.service.getById(this.route.snapshot.params['id']).subscribe(
      (horse: Horse) => {
        this.horse = horse;
      });
  }

  delete(id: number) {
    let observable: Observable<Horse> = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse was successfully deleted`);
        this.router.navigate(['/horses']);
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error(this.errorFormatter.format(error), "Horse could not be deleted", {
          enableHtml: true,
          timeOut: 10000,
        });
      }
    });
  }

  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  edit(id: number) {
    this.router.navigate(['/horses/edit/' + id]);
  }

}
