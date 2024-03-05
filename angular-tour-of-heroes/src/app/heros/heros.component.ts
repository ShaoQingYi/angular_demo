import { Component, OnInit } from '@angular/core';
import {Hero} from '../Hero';
import { HEROES } from '../mock-heroes';

@Component({
  selector: 'app-heros',
  templateUrl: './heros.component.html',
  styleUrls: ['./heros.component.css']
})
export class HerosComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  // hero = "Windstorm";
  // hero : Hero = {
  //   id :1,
  //   name : 'Windstorm'
  // }

  heroes = HEROES

  selectedHero?: Hero;
  onSelect(hero: Hero): void {
    this.selectedHero = hero
  }

}
