import { Component, OnInit } from '@angular/core';
import {Hero} from '../Hero';
<<<<<<< HEAD
import { HeroService } from '../hero.service';
=======
import { HEROES } from '../mock-heroes';
>>>>>>> refs/remotes/origin/master

@Component({
  selector: 'app-heros',
  templateUrl: './heros.component.html',
  styleUrls: ['./heros.component.css']
})
export class HerosComponent implements OnInit {

<<<<<<< HEAD
  constructor(private heroService : HeroService) { }

  ngOnInit(): void {
    this.getHeroes();
=======
  constructor() { }

  ngOnInit(): void {
>>>>>>> refs/remotes/origin/master
  }

  // hero = "Windstorm";
  // hero : Hero = {
  //   id :1,
  //   name : 'Windstorm'
  // }

<<<<<<< HEAD
  heroes : Hero[] = [];
=======
  heroes = HEROES
>>>>>>> refs/remotes/origin/master

  selectedHero?: Hero;
  onSelect(hero: Hero): void {
    this.selectedHero = hero
  }

<<<<<<< HEAD
  getHeroes(): void {
    this.heroes = this.heroService.getHeroes();
  }

=======
>>>>>>> refs/remotes/origin/master
}
