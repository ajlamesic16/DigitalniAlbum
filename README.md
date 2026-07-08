# Digitalni Album

Digitalni Album je mobilna Android aplikacija za skupljanje digitalnih fudbalskih sličica. Aplikacija omogućava pregled albuma, otvaranje paketića, označavanje favorita, praćenje statistike kolekcije i razmjenu duplikata za tokene.

## Opis aplikacije

Aplikacija simulira digitalni album fudbalskih sličica. Korisnik može pregledati sve sličice, filtrirati ih po reprezentacijama, sortirati ih po broju ili imenu, otvoriti detalje svake sličice i označiti omiljene sličice.
Pri prvom pokretanju korisnik bira omiljenu reprezentaciju, koja se zatim posebno označava u albumu. Podaci o korisničkim postavkama, tokenima i dnevnom limitu paketića čuvaju se lokalno.

## Glavne funkcionalnosti

- prikaz albuma sličica
- filter po reprezentaciji
- sortiranje po broju i imenu
- detaljan prikaz sličice
- označavanje favorita
- pregled omiljenih sličica
- otvaranje paketića kroz grebalicu
- dnevni limit otvaranja paketića
- statistika kolekcije sa grafičkim prikazom
- razmjena duplikata za tokene
- otključavanje sličica koje nedostaju
- izbor omiljene reprezentacije pri prvom pokretanju
- postavke prikaza liste ili mreže
- dijeljenje sličice putem sistemskog share menija

## Korištene tehnologije

- Kotlin
- Jetpack Compose
- Material 3
- Navigation for Compose
- MVVM arhitektura
- ViewModel
- StateFlow i Flow
- Kotlin Coroutines
- Retrofit
- Room database
- DataStore Preferences
- Coil za prikaz slika
- Compose Preview

## Arhitektura

Aplikacija koristi MVVM arhitekturu.
- Model predstavlja podatke aplikacije, kao što su sličice, rijetkost sličice i lokalni Room entiteti.
- View predstavlja Compose ekrane i komponente korisničkog interfejsa.
- ViewModel upravlja stanjem ekrana i poziva repozitorij za podatke.
- Repository predstavlja jedinstveni izvor podataka i povezuje API, Room bazu i poslovnu logiku aplikacije.

## Podaci i lokalno čuvanje

Podaci o sličicama se dobavljaju sa API-ja pomoću Retrofit biblioteke. Nakon preuzimanja, sličice se čuvaju u lokalnoj Room bazi, što omogućava prikaz već učitanih podataka i kada aplikacija nema internet konekciju.
Korisničke postavke, kao što su odabrana omiljena reprezentacija, način prikaza albuma, tokeni i dnevni limit paketića, čuvaju se pomoću DataStore Preferences.

## API integracija

Aplikacija koristi Retrofit za komunikaciju sa udaljenim JSON API-jem. API se koristi za:
- preuzimanje liste igrača
- preuzimanje grbova reprezentacija
- otvaranje paketića sa nasumičnim sličicama

DTO klase predstavljaju podatke koji dolaze sa API-ja, a zatim se mapiraju u lokalne Room entitete i domenski model aplikacije.

## Navigacija

Navigacija između ekrana urađena je pomoću Navigation for Compose. Aplikacija sadrži više ekrana:
- Splash screen
- Onboarding
- Album
- Detalji sličice
- Favoriti
- Statistika
- Razmjena
- Postavke
- Otvaranje paketića

Podaci se između ekrana prosljeđuju kroz navigacione argumente, npr. `stickerId` za prikaz detalja odabrane sličice.

## Upravljanje stanjem

Aplikacija koristi Compose state management kroz `StateFlow`, `collectAsStateWithLifecycle`, `remember` i `mutableStateOf`. Promjena stanja u ViewModel-u automatski pokreće recomposition i ažurira korisnički interfejs.

## Lokalna baza

Room baza se koristi za lokalno čuvanje sličica. Baza sadrži informacije o:
- nazivu sličice
- reprezentaciji
- slici
- broju u albumu
- rijetkosti
- poziciji igrača
- statusu posjedovanja
- broju duplikata
- statusu favorita
