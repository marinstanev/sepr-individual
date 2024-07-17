import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map, Observable, throwError} from 'rxjs';
import {formatIsoDate} from '../util/date-helper';
import {
  TournamentCreateDto, TournamentDetailDto, TournamentDetailParticipantDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto, TournamentStandingsTreeDto
} from "../dto/tournament";
const baseUri = environment.backendUrl + '/tournaments';

class ErrorDto {
  constructor(public message: String) {}
}

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * Get tournaments stored in the system with the specified parameters.
   *
   * @param searchParams the specified parameters of the tournaments that should be listed
   * @return an Observable for the specified tournaments
   */
  // \TEMPLATE EXCLUDE END\
  public search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.startDate) {
      params = params.append('startDate', formatIsoDate(searchParams.startDate));
    }
    if (searchParams.endDate) {
      params = params.append('endDate', formatIsoDate(searchParams.endDate));
    }
    return this.http.get<TournamentListDto[]>(baseUri, { params });
  }
  // \TEMPLATE EXCLUDE END\

  /**
   * Create a new tournament in the system.
   *
   * @param tournament the data for the tournament that should be created
   * @return an Observable for the created tournament
   */
  public create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    return this.http.post<TournamentDetailDto>(
      baseUri,
      tournament
    );
  }

  /**
   * Get the tournament-standings with the ID given from the system.
   *
   * @param id the ID of the tournament-standings to get
   * @return on Observable the tournament-standings with the ID given
   */
  getTournamentStandings(id: string | null): Observable<TournamentStandingsDto> {
    return this.http.get<TournamentStandingsDto>(
      `${baseUri}/standings/${id}`
    );
  }

  /**
   * Update data in the system linked to the tournament-standings with the ID given based on the data given in {@code standings}.
   *
   * @param id the id of the tournament-standings to update
   * @param standings the tournament-standings to update
   * @return on Observable of the updated tournament-standings
   */
  update(id: string | null, standings: TournamentStandingsDto | undefined): Observable<TournamentStandingsDto> {
    return this.http.put<TournamentStandingsDto>(
      `${baseUri}/standings/${id}`,
      standings
    );
  }

  /**
   * Generate the first round of tournament-standings based on data stored in the system
   * and update data in the system.
   *
   * @param id the id of the tournament-standings which first round should be generated
   * @param  standings the tournament-standings which first round should be generated
   * @return on Observable the updated tournament-standings
   */
  generateFirstRound(id: string | null, standings: TournamentStandingsDto | undefined): Observable<TournamentStandingsDto> {
    return this.http.put<TournamentStandingsDto>(
      `${baseUri}/firstRound/${id}`,
      standings
    );
  }
}
