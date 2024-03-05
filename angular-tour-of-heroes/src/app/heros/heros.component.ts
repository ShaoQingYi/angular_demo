import { Component, OnInit } from '@angular/core';
import {Hero} from '../Hero';
import { HeroService } from '../hero.service';

@Component({
  selector: 'app-heros',
  templateUrl: './heros.component.html',
  styleUrls: ['./heros.component.css']
})
export class HerosComponent implements OnInit {

  constructor(private heroService : HeroService) { }

  ngOnInit(): void {
    this.getHeroes();
  }

  // hero = "Windstorm";
  // hero : Hero = {
  //   id :1,
  //   name : 'Windstorm'
  // }

  heroes : Hero[] = [];

  selectedHero?: Hero;
  onSelect(hero: Hero): void {
    this.selectedHero = hero
  }

  getHeroes(): void {
    this.heroes = this.heroService.getHeroes();
  }

}
