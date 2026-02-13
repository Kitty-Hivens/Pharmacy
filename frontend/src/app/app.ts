import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent {
  title = 'frontend';

  constructor(translate: TranslateService) {
    translate.addLangs(['en', 'ru']);
    translate.setFallbackLang('en');

    const savedLang = localStorage.getItem('app-lang');
    const browserLang = translate.getBrowserLang();
    const langToUse = savedLang
      ? savedLang
      : (browserLang?.match(/en|ru/) ? browserLang : 'en');

    translate.use(langToUse);
  }
}
