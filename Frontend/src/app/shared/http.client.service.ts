import {Injectable, Injector} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {FormGroup} from '@angular/forms';
import {Observable} from "rxjs";
import {Router} from '@angular/router';
import {LoginRequest} from "./data/login-request";
import {JwtData} from "./data/jwt-data";
import {LoginResult} from "./data/login-result";
import {AuthService, SessionExpiredException, UnauthorizedException} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  private readonly URL_LOGIN:   string = 'http://localhost:8090/oauth/token';
  private readonly URL_SCHEMAS: string = 'http://localhost:8082/api/search';
  private readonly URL_SEARCH:  string = 'http://localhost:8082/api/search';
  private readonly URL_DATA:  string = 'http://localhost:8081/api/data';

  constructor(private router: Router, private http: HttpClient, private injector: Injector) {
  }

  private getHeadersWithToken(): HttpHeaders {
    const authService = this.injector.get(AuthService);

    try {
      const accessToken = authService.validateAndGetToken();
      return new HttpHeaders({'Authorization': `Bearer ${accessToken}`});
    } catch (e: UnauthorizedException | SessionExpiredException | any) {
      alert(e.message);
      authService.performLogout();
      throw e;
    }
  }

  private getMultipartRequestOptions(): Object {
    const headersWithToken = this.getHeadersWithToken();
    headersWithToken.set('Content-Type', 'multipart/form-data');

    return {
      "responseType": "json",
      "headers": headersWithToken
    }
  }

  private getJSONRequestOptions(): Object {
    const headersWithToken = this.getHeadersWithToken();
    headersWithToken.set('Content-Type', 'application/json');

    return {
      "responseType": "json",
      "headers": headersWithToken
    }
  }

  private getRequest<T>(url: string): Observable<T> {
    return this.http.get<T>(url, this.getJSONRequestOptions());
  }

  private postRequest<T>(url: string, params: any): Observable<T> {
    return this.http.post<T>(url, JSON.stringify(params, null, 2), this.getJSONRequestOptions());
  }
  private postFile<T>(url: string, formData: FormData): Observable<T> {
    return this.http.post<T>(url, formData, this.getMultipartRequestOptions());
  }

  login(email: string, password: string, callback: Function): void {
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('client-ui:secret'),
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    const params = new LoginRequest(email, password);
    this
      .http
      .post<JwtData>(this.URL_LOGIN, params, {headers})
      .subscribe({
        next: jwtData => {
          callback.call(this, LoginResult.success(jwtData))
        },
        error: err => {
          callback.call(this, LoginResult.error(err.error.error_description || "Server is temporary unavailable. Please, try again later!"));
        }
      });
  }

  public getSchemas<T>(): Observable<T> {
    return this.getRequest(this.URL_SCHEMAS);
  }

  public search<T>(body: FormGroup): Observable<T> {
    return this.postRequest(this.URL_SEARCH, body.value);
  }


  public sendSchema<T>(formData: FormData): Observable<T> {
    return this.postFile(this.URL_DATA, formData);
  }
}
