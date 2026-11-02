import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { LoginResponse, UserRole } from './models';

const SESSION_KEY = 'question-paper-session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private session: LoginResponse | null = this.readSession();

  constructor(private readonly http: HttpClient) {}

  get isAuthenticated(): boolean {
    return this.session !== null;
  }

  get username(): string {
    return this.session?.username ?? '';
  }

  get role(): UserRole | null {
    return this.session?.role ?? null;
  }

  get token(): string | null {
    return this.session?.token ?? null;
  }

  login(username: string, password: string) {
    return this.http
      .post<LoginResponse>('/api/auth/login', { username, password })
      .pipe(
        tap((session) => {
          this.session = session;
          sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
        })
      );
  }

  logout(): void {
    this.session = null;
    sessionStorage.removeItem(SESSION_KEY);
  }

  private readSession(): LoginResponse | null {
    const stored = sessionStorage.getItem(SESSION_KEY);
    if (!stored) return null;

    try {
      const session = JSON.parse(stored) as LoginResponse;
      return session.token && session.username && session.role ? session : null;
    } catch {
      sessionStorage.removeItem(SESSION_KEY);
      return null;
    }
  }
}
