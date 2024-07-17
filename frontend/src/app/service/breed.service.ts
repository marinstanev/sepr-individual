import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Breed} from "../dto/breed";
import {Observable} from "rxjs";

const baseUri = environment.backendUrl + "/breeds";

@Injectable({
  providedIn: 'root'
})
export class BreedService {


  constructor(
    private http: HttpClient
  ) {
  }

  /**
   * Retrieve all breeds from the persistent data store based on the name given.
   *
   * @param name the name based on which breeds should be retrieved
   * @param limit the maximum number of breeds that should be retrieved
   * @return an Observable of array of found breeds
   */
  public breedsByName(name: string, limit: number | undefined): Observable<Breed[]> {
    let params = new HttpParams();
    params = params.append("name", name);
    if (limit != null) {
      params = params.append("limit", limit);
    }
    return this.http.get<Breed[]>(baseUri, { params });
  }
}
