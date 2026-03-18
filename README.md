# 📝 ToDoMaster - Gestionnaire de Tâches Android

Une application Android moderne, intuitive et performante pour gérer ses tâches au quotidien. Ce projet met en œuvre les meilleurs standards de développement Android actuels, avec une architecture robuste et une interface utilisateur entièrement réactive.

## ✨ Fonctionnalités

Cette application propose une gestion avancée des tâches (CRUD complet) avec des fonctionnalités poussées pour optimiser la productivité :

* ✅ **Gestion des statuts** : Marquer une tâche comme terminée, à faire, ou suivre son avancement.
* 📅 **Échéances & Calendrier** : Définition d'une date limite pour chaque tâche via un `DatePicker` natif.
* 🚦 **Code Couleur Intelligent** : 
    * 🔵 **Bleu** : Tâche standard.
    * 🟠 **Orange (🔥)** : Tâche urgente (échéance dans moins de 48h).
    * 🔴 **Rouge (⚠️)** : Tâche en retard.
    * 🟢 **Vert (✅)** : Tâche terminée.
* 🔍 **Recherche & Filtrage** : Barre de recherche textuelle en temps réel et filtres rapides (Toutes, À faire, Fini, Urgentes).
* ↕️ **Tri Dynamique** : Organisation des tâches par date (croissant/décroissant) ou par ordre alphabétique.
* 🌓 **Thème Personnalisé** : Interface avec un thème "Blanc Crème" premium et un véritable "Mode Sombre" (basculable manuellement dans les paramètres).
* 🧭 **Navigation Fluide** : Utilisation d'une `BottomNavigationBar` pour naviguer entre l'accueil, le tableau de gestion et les préférences du compte.

## 🛠️ Stack Technique & Architecture

Le projet est développé en **Kotlin** natif et respecte l'architecture **MVVM (Model-View-ViewModel)** recommandée par Google.

* **UI Toolkit** : [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3) pour une interface déclarative et réactive.
* **Base de données locale** : [Room Database](https://developer.android.com/training/data-storage/room) (Abstraction SQLite) pour la persistance des données.
* **Programmation Asynchrone** : [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Kotlin Flows](https://kotlinlang.org/docs/flow.html) pour une manipulation fluide des données en temps réel.
* **Navigation** : Jetpack Navigation Compose pour la gestion des écrans et de la barre de navigation.
* **Architecture** : Modèle MVVM garantissant une séparation claire entre la logique métier (`ViewModel`), l'accès aux données (`DAO`/`Entity`) et l'interface (`Screens`).

## 🚀 Installation et Lancement

1. Cloner ce dépôt sur votre machine locale :
   ```bash
   git clone [https://github.com/votre-nom/ToDoMaster.git](https://github.com/votre-nom/ToDoMaster.git)
