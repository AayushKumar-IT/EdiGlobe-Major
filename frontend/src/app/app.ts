import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter, finalize, Observable } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiService, PaperRequest, QuestionInput, SubjectInput, UserInput } from './api.service';
import { AuthService } from './auth.service';
import { Difficulty, GeneratedPaper, Question, Subject, TopicAllocation, User } from './models';

@Component({
  selector: 'app-root',
  imports: [CommonModule, FormsModule, RouterOutlet],
  templateUrl: './app.html'
})
export class App implements OnInit {
  readonly difficulties: Difficulty[] = ['EASY', 'MEDIUM', 'HARD'];
  readonly navItems: Array<{ id: string; label: string; icon: string; adminOnly?: boolean }> = [
    { id: 'overview', label: 'Overview', icon: '⌂' },
    { id: 'questions', label: 'Question bank', icon: '▤' },
    { id: 'subjects', label: 'Subjects', icon: '◫' },
    { id: 'generate', label: 'Generate paper', icon: '✳' },
    { id: 'papers', label: 'Paper history', icon: '◷' },
    { id: 'users', label: 'User management', icon: '♙', adminOnly: true }
  ];

  page = 'overview';
  subjects: Subject[] = [];
  questions: Question[] = [];
  papers: GeneratedPaper[] = [];
  users: User[] = [];
  generatedPaper: GeneratedPaper | null = null;
  busy = false;
  error = '';
  notice = '';
  loginUsername = '';
  loginPassword = '';
  loginBusy = false;
  loginError = '';

  searchText = '';
  questionSubjectFilter = '';
  questionDifficultyFilter = '';
  questionDialogOpen = false;
  editingQuestionId: number | null = null;
  questionForm: QuestionInput = this.emptyQuestion();

  subjectDialogOpen = false;
  editingSubjectId: number | null = null;
  subjectForm: SubjectInput = { name: '', description: '' };
  userDialogOpen = false;
  userForm: UserInput = { username: '', password: '', role: 'FACULTY' };

  paperTitle = 'Question Paper';
  paperSubjectId = 0;
  paperDifficulty: Difficulty | 'ANY' = 'ANY';
  paperTotalMarks = 50;
  allocations: TopicAllocation[] = [];

  constructor(
    readonly auth: AuthService,
    private readonly api: ApiService,
    private readonly router: Router,
    private readonly destroyRef: DestroyRef
  ) {}

  ngOnInit(): void {
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.applyRoute());
    this.applyRoute();
    if (this.auth.isAuthenticated) this.loadWorkspace();
  }

  get isAdmin(): boolean {
    return this.auth.role === 'ADMIN';
  }

  get filteredQuestions(): Question[] {
    const search = this.searchText.trim().toLowerCase();
    return this.questions.filter((question) => {
      const matchesSubject =
        !this.questionSubjectFilter || String(question.subjectId) === this.questionSubjectFilter;
      const matchesDifficulty =
        !this.questionDifficultyFilter || question.difficultyLevel === this.questionDifficultyFilter;
      const matchesSearch =
        !search ||
        `${question.questionText} ${question.topic} ${this.subjectName(question.subjectId)}`
          .toLowerCase()
          .includes(search);
      return matchesSubject && matchesDifficulty && matchesSearch;
    });
  }

  get selectedSubject(): Subject | undefined {
    return this.subjects.find((subject) => subject.id === Number(this.paperSubjectId));
  }

  get subjectTopics(): string[] {
    if (!this.paperSubjectId) return [];
    return [...new Set(
      this.questions
        .filter((question) => question.subjectId === Number(this.paperSubjectId))
        .map((question) => question.topic.trim())
        .filter(Boolean)
    )].sort((a, b) => a.localeCompare(b));
  }

  get allocatedMarks(): number {
    return this.allocations.reduce((sum, allocation) => sum + Number(allocation.marks || 0), 0);
  }

  get allocationTotalMismatch(): boolean {
    return this.allocatedMarks !== Number(this.paperTotalMarks);
  }

  questionCountFor(subjectId: number): number {
    return this.questions.filter((question) => question.subjectId === subjectId).length;
  }

  get canGenerate(): boolean {
    return (
      Number(this.paperSubjectId) > 0 &&
      this.paperTitle.trim().length > 0 &&
      Number(this.paperTotalMarks) > 0 &&
      this.allocations.length > 0 &&
      this.allocatedMarks === Number(this.paperTotalMarks) &&
      this.allocations.some((allocation) => Number(allocation.marks) > 0) &&
      Number(this.paperTotalMarks) <= 1000
    );
  }

  login(): void {
    this.loginError = '';
    this.loginBusy = true;
    this.auth
      .login(this.loginUsername.trim(), this.loginPassword)
      .pipe(finalize(() => (this.loginBusy = false)))
      .subscribe({
        next: () => {
          this.loginPassword = '';
          this.loadWorkspace();
          this.router.navigateByUrl('/overview');
        },
        error: (error) => (this.loginError = this.errorMessage(error, 'Unable to sign in. Check your credentials.'))
      });
  }

  logout(): void {
    this.auth.logout();
    this.subjects = [];
    this.questions = [];
    this.papers = [];
    this.users = [];
    this.router.navigateByUrl('/login');
  }

  navigate(page: string): void {
    if (page === 'users' && !this.isAdmin) return;
    if (!this.navItems.some((item) => item.id === page)) return;
    this.error = '';
    this.notice = '';
    this.router.navigateByUrl(`/${page}`);
  }

  private applyRoute(): void {
    const page = this.router.routerState.snapshot.root.firstChild?.data['page'];
    this.page = typeof page === 'string' ? page : 'overview';
    if (this.page === 'generate') this.prepareAllocations();
    if (this.page === 'users' && this.isAdmin) this.loadUsers();
  }

  loadUsers(): void {
    this.api.getUsers().subscribe({
      next: (users) => (this.users = users),
      error: (error) => (this.error = this.errorMessage(error, 'Could not load users.'))
    });
  }

  openNewUser(): void {
    this.userForm = { username: '', password: '', role: 'FACULTY' };
    this.userDialogOpen = true;
  }

  saveUser(): void {
    this.api.createUser(this.userForm).subscribe({
      next: () => {
        this.userDialogOpen = false;
        this.notice = 'User account created.';
        this.loadUsers();
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not create this user.'))
    });
  }

  deleteUser(user: User): void {
    if (user.username === this.auth.username) {
      this.error = 'You cannot delete your own account.';
      return;
    }
    if (!window.confirm(`Delete the ${user.role.toLowerCase()} account "${user.username}"?`)) return;
    this.api.deleteUser(user.id).subscribe({
      next: () => {
        this.notice = 'User account deleted.';
        this.loadUsers();
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not delete this user.'))
    });
  }

  updateUserRole(user: User, role: 'ADMIN' | 'FACULTY'): void {
    if (user.username === this.auth.username) {
      this.error = 'You cannot change your own account role.';
      return;
    }
    this.api.updateUserRole(user.id, role).subscribe({
      next: (updated) => {
        this.users = this.users.map((current) => current.id === updated.id ? updated : current);
        this.notice = 'User role updated.';
      },
      error: (error) => {
        this.error = this.errorMessage(error, 'Could not update this user role.');
        this.loadUsers();
      }
    });
  }

  loadWorkspace(): void {
    this.busy = true;
    this.error = '';
    let pending = 3;
    const finish = () => {
      pending -= 1;
      if (pending === 0) this.busy = false;
    };

    this.api.getSubjects().subscribe({
      next: (subjects) => {
        this.subjects = subjects;
        if (!this.paperSubjectId && subjects.length) this.paperSubjectId = subjects[0].id;
        this.prepareAllocations();
        finish();
      },
      error: (error) => {
        this.error = this.errorMessage(error, 'Could not load subjects.');
        finish();
      }
    });
    this.api.getQuestions().subscribe({
      next: (questions) => {
        this.questions = questions;
        this.prepareAllocations();
        finish();
      },
      error: (error) => {
        this.error = this.errorMessage(error, 'Could not load questions.');
        finish();
      }
    });
    this.api.getPapers().subscribe({
      next: (papers) => {
        this.papers = papers;
        finish();
      },
      error: (error) => {
        this.error = this.errorMessage(error, 'Could not load saved papers.');
        finish();
      }
    });
  }

  subjectName(id: number): string {
    return this.subjects.find((subject) => subject.id === id)?.name ?? 'Unknown subject';
  }

  openNewQuestion(): void {
    this.editingQuestionId = null;
    this.questionForm = this.emptyQuestion();
    if (this.subjects.length) this.questionForm.subjectId = this.subjects[0].id;
    this.questionDialogOpen = true;
  }

  editQuestion(question: Question): void {
    this.editingQuestionId = question.id;
    this.questionForm = {
      questionText: question.questionText,
      subjectId: question.subjectId,
      topic: question.topic,
      difficultyLevel: question.difficultyLevel,
      marks: question.marks
    };
    this.questionDialogOpen = true;
  }

  saveQuestion(): void {
    const request = this.editingQuestionId
      ? this.api.updateQuestion(this.editingQuestionId, this.questionForm)
      : this.api.createQuestion(this.questionForm);
    request.subscribe({
      next: () => {
        this.questionDialogOpen = false;
        this.notice = this.editingQuestionId ? 'Question updated.' : 'Question added to the bank.';
        this.loadWorkspace();
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not save this question.'))
    });
  }

  deleteQuestion(question: Question): void {
    if (!window.confirm('Delete this question from the bank?')) return;
    this.api.deleteQuestion(question.id).subscribe({
      next: () => {
        this.notice = 'Question deleted.';
        this.loadWorkspace();
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not delete this question.'))
    });
  }

  openNewSubject(): void {
    this.editingSubjectId = null;
    this.subjectForm = { name: '', description: '' };
    this.subjectDialogOpen = true;
  }

  editSubject(subject: Subject): void {
    this.editingSubjectId = subject.id;
    this.subjectForm = { name: subject.name, description: subject.description ?? '' };
    this.subjectDialogOpen = true;
  }

  saveSubject(): void {
    const request = this.editingSubjectId
      ? this.api.updateSubject(this.editingSubjectId, this.subjectForm)
      : this.api.createSubject(this.subjectForm);
    request.subscribe({
      next: () => {
        this.subjectDialogOpen = false;
        this.notice = this.editingSubjectId ? 'Subject updated.' : 'Subject created.';
        this.loadWorkspace();
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not save this subject.'))
    });
  }

  deleteSubject(subject: Subject): void {
    if (!window.confirm(`Delete "${subject.name}"? Subjects with questions may not be removable.`)) return;
    this.api.deleteSubject(subject.id).subscribe({
      next: () => {
        this.notice = 'Subject deleted.';
        this.loadWorkspace();
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not delete this subject.'))
    });
  }

  onPaperSubjectChange(): void {
    this.generatedPaper = null;
    this.prepareAllocations();
  }

  onTotalMarksChange(): void {
    this.distributeMarks();
  }

  onAllocationChange(): void {
    const remaining = Number(this.paperTotalMarks) - this.allocatedMarks;
    if (remaining > 0) {
      const zeroAllocation = this.allocations.find((allocation) => Number(allocation.marks) === 0);
      if (zeroAllocation) zeroAllocation.marks = remaining;
    }
  }

  generatePaper(): void {
    if (!this.canGenerate) return;
    this.error = '';
    this.generatedPaper = null;
    const request: PaperRequest = {
      title: this.paperTitle.trim(),
      subjectId: Number(this.paperSubjectId),
      totalMarks: Number(this.paperTotalMarks),
      difficultyLevel: this.paperDifficulty,
      topicAllocations: this.allocations
        .filter((allocation) => Number(allocation.marks) > 0)
        .map(({ topic, marks }) => ({ topic, marks: Number(marks) }))
    };
    this.api.generatePaper(request).subscribe({
      next: (paper) => {
        this.generatedPaper = paper;
        this.papers = [paper, ...this.papers.filter((saved) => saved.id !== paper.id)];
        this.notice = 'Your question paper is ready.';
      },
      error: (error) => (this.error = this.errorMessage(error, 'Paper generation failed. Review the available questions and criteria.'))
    });
  }

  downloadPaper(paper: GeneratedPaper): void {
    this.downloadFile(this.api.downloadPaper(paper.id), `${this.safeFileName(paper.title)}.pdf`);
  }

  downloadPaperWord(paper: GeneratedPaper): void {
    this.downloadFile(this.api.downloadPaperWord(paper.id), `${this.safeFileName(paper.title)}.docx`);
  }

  private downloadFile(request: Observable<Blob>, filename: string): void {
    request.subscribe({
      next: (file) => {
        const url = URL.createObjectURL(file);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = filename;
        anchor.click();
        URL.revokeObjectURL(url);
      },
      error: (error) => (this.error = this.errorMessage(error, 'Could not download this paper.'))
    });
  }

  trackById(_index: number, item: { id: number }): number {
    return item.id;
  }

  private prepareAllocations(): void {
    const topics = this.subjectTopics;
    if (!topics.length) {
      this.allocations = [];
      return;
    }
    const existing = new Map(this.allocations.map((allocation) => [allocation.topic, allocation.marks]));
    this.allocations = topics.map((topic) => ({ topic, marks: existing.get(topic) ?? 0 }));
    if (this.allocations.every((allocation) => allocation.marks === 0)) this.distributeMarks();
  }

  private distributeMarks(): void {
    if (!this.allocations.length) return;
    const total = Math.max(0, Number(this.paperTotalMarks) || 0);
    const base = Math.floor(total / this.allocations.length);
    let remainder = total % this.allocations.length;
    this.allocations = this.allocations.map((allocation) => {
      const marks = base + (remainder > 0 ? 1 : 0);
      remainder = Math.max(0, remainder - 1);
      return { ...allocation, marks };
    });
  }

  private emptyQuestion(): QuestionInput {
    return { questionText: '', subjectId: 0, topic: '', difficultyLevel: 'MEDIUM', marks: 5 };
  }

  private errorMessage(error: { error?: { message?: string; detail?: string }; message?: string }, fallback: string): string {
    return error.error?.message ?? error.error?.detail ?? error.message ?? fallback;
  }

  private safeFileName(title: string): string {
    return title.trim().replace(/[<>:"/\\|?*]+/g, '-').replace(/\s+/g, '-').toLowerCase() || 'question-paper';
  }
}
