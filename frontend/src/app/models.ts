export type UserRole = 'ADMIN' | 'FACULTY';
export type Difficulty = 'EASY' | 'MEDIUM' | 'HARD';

export interface LoginResponse {
  token: string;
  username: string;
  role: UserRole;
}

export interface User {
  id: number;
  username: string;
  role: UserRole;
}

export interface Subject {
  id: number;
  name: string;
  description: string;
}

export interface Question {
  id: number;
  questionText: string;
  subjectId: number;
  subjectName?: string;
  topic: string;
  difficultyLevel: Difficulty;
  marks: number;
}

export interface TopicAllocation {
  topic: string;
  marks: number;
}

export interface GeneratedPaper {
  id: number;
  title: string;
  subjectId: number;
  subjectName?: string;
  generatedBy?: string;
  generatedDate?: string;
  totalMarks: number;
  questions: GeneratedPaperQuestion[];
}

export interface GeneratedPaperQuestion {
  id: number;
  questionText: string;
  topic: string;
  difficultyLevel: Difficulty;
  marks: number;
  displayOrder: number;
}
