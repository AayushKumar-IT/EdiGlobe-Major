import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router, provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { App } from './app';
import { PageRouteComponent, routes } from './app.routes';

describe('App', () => {
  beforeEach(async () => {
    sessionStorage.removeItem('question-paper-session');
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideRouter(routes)],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the sign-in form while signed out', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Sign in to your workspace');
    expect(compiled.querySelector('input[autocomplete="username"]')).not.toBeNull();
  });

  it('redirects signed-out visitors from workspace routes to login', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/questions', PageRouteComponent);

    expect(TestBed.inject(Router).url).toBe('/login');
  });

  it('redirects faculty away from administrator-only routes', async () => {
    sessionStorage.setItem(
      'question-paper-session',
      JSON.stringify({ token: 'test-token', username: 'faculty', role: 'FACULTY' })
    );
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/users', PageRouteComponent);

    expect(TestBed.inject(Router).url).toBe('/overview');
  });
});
