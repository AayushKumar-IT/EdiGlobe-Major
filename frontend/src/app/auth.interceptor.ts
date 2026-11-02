import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = sessionStorage.getItem('question-paper-session');
  if (!token) return next(request);

  try {
    const session = JSON.parse(token) as { token?: string };
    if (!session.token) return next(request);
    return next(request.clone({ setHeaders: { Authorization: `Bearer ${session.token}` } }));
  } catch {
    sessionStorage.removeItem('question-paper-session');
    return next(request);
  }
};
