import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { User, UserCompat } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  user: UserCompat | null = null;

  ngOnInit(): void {
    const savedUser = localStorage.getItem('loggedInUser');

    if (savedUser) {
      const user = JSON.parse(savedUser);
      // Convert to compatible format
      this.user = {
        ...user,
        name: user.fullName || user.name || 'User'
      };
    }
  }
}