import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';

import { WasteType } from '../../models/waste-type.model';
import { WasteTypesService } from '../../services/waste-types';

@Component({
  selector: 'app-waste-types',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './waste-types.html',
  styleUrl: './waste-types.scss',
})
export class WasteTypes {
  private wasteTypesService = inject(WasteTypesService);

  wasteTypes = toSignal<WasteType[]>(
    this.wasteTypesService.list(),
  );

  getIcon(name: string): string {
    const icons: Record<string, string> = {
      'Celulares': '📱',
      'Computadores': '💻',
      'Monitores': '🖥️',
      'Pilhas e baterias': '🔋',
      'Cabos e carregadores': '🔌',
      'Impressoras': '🖨️',
      'Teclados': '⌨️',
      'Mouses': '🖱️',
    };

    return icons[name] || '♻️';
  }
}