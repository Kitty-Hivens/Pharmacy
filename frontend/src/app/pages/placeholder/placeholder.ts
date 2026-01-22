import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-placeholder',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex flex-column align-items-center justify-content-center h-full p-6 text-center">
      <i class="pi pi-cog spin-slow text-600 mb-4" style="font-size: 4rem"></i>
      <h2 class="text-900 font-bold text-3xl mb-2">Development in Progress</h2>
      <p class="text-600 text-xl max-w-30rem">
        This module is part of the Enterprise roadmap. Check back in v1.1.
      </p>
    </div>
  `,
  styles: [`
    .spin-slow { animation: spin 4s linear infinite; }
    @keyframes spin { 100% { transform: rotate(360deg); } }
  `]
})
export class PlaceholderComponent {}
