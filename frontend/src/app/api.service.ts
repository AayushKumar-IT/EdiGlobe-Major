import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Difficulty,
  GeneratedPaper,
  Question,
  Subject,
  TopicAllocation,
  User
} from './models';

export interface QuestionInput {
  questionText: string;
  subjectId: number;
  topic: string;
  difficultyLevel: Difficulty;
  marks: number;
}

export interface SubjectInput {
  name: string;
  description: string;
}

export interface UserInput {
  username: string;
  password: string;
  role: 'ADMIN' | 'FACULTY';
}

export interface PaperRequest {
  title: string;
  subjectId: number;
  totalMarks: number;
  difficultyLevel: Difficulty | 'ANY';
  topicAllocations: TopicAllocation[];
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  getSubjects() {
    return this.http.get<Subject[]>('/api/subjects');
  }

  createSubject(input: SubjectInput) {
    return this.http.post<Subject>('/api/subjects', input);
  }

  updateSubject(id: number, input: SubjectInput) {
    return this.http.put<Subject>(`/api/subjects/${id}`, input);
  }

  deleteSubject(id: number) {
    return this.http.delete<void>(`/api/subjects/${id}`);
  }

  getQuestions(filters?: { subjectId?: number; difficultyLevel?: string; search?: string }) {
    let params = new HttpParams();
    if (filters?.subjectId) params = params.set('subjectId', filters.subjectId);
    if (filters?.difficultyLevel) params = params.set('difficultyLevel', filters.difficultyLevel);
    if (filters?.search) params = params.set('search', filters.search);
    return this.http.get<Question[]>('/api/questions', { params });
  }

  createQuestion(input: QuestionInput) {
    return this.http.post<Question>('/api/questions', input);
  }

  updateQuestion(id: number, input: QuestionInput) {
    return this.http.put<Question>(`/api/questions/${id}`, input);
  }

  deleteQuestion(id: number) {
    return this.http.delete<void>(`/api/questions/${id}`);
  }

  generatePaper(input: PaperRequest) {
    return this.http.post<GeneratedPaper>('/api/papers/generate', input);
  }

  getPapers() {
    return this.http.get<GeneratedPaper[]>('/api/papers');
  }

  downloadPaper(id: number): Observable<Blob> {
    return this.http.get(`/api/papers/${id}/pdf`, { responseType: 'blob' });
  }

  downloadPaperWord(id: number): Observable<Blob> {
    return this.http.get(`/api/papers/${id}/docx`, { responseType: 'blob' });
  }

  getUsers() {
    return this.http.get<User[]>('/api/admin/users');
  }

  createUser(input: UserInput) {
    return this.http.post<User>('/api/admin/users', input);
  }

  deleteUser(id: number) {
    return this.http.delete<void>(`/api/admin/users/${id}`);
  }

  updateUserRole(id: number, role: 'ADMIN' | 'FACULTY') {
    return this.http.put<User>(`/api/admin/users/${id}/role`, { role });
  }
}
