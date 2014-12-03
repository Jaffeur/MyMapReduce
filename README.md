 # Projet SHAVADOOP
 
Implementation de Map Reduce en Java

 

## 1.  Introduction

Dans le cadre de ce projet j'ai implémenté un programme qui suit l'architecture Map Reduce du papier de Google. Ce programme est codé en Java et test un « word-count » sur un document donné et retourne un fichier avec le décompte des mots.

Dans l'architecture de ce programme il y a un Master qui dirige le job Map-Reduce et les Slaves qui executes des tâches qui peuvent être distribuées sur d'autre machines distantes.

Le master lit et découpe - selon un nombre de lignes données - le texte en entré, et envoi de manière distribuée les lignes aux Slaves qui vont "Spliter" les données et les écrire dans un fichier sous forme Clé(mot)-Valeur(nombre d'occurence de mots). Cette étape est le Map.
A la fin de chaque "Split" le Master lit le fichier et construit un dictionnaire qui va referencer les mots et et fichiers dans lesquels ils se trouvent. Clé(mot) - Valeur (adresse du fichier)

Quand tous les Maps sont finis, pour chaque mot dans le dictionnaire le Master va lancer un Reduce sur une machine distante: les machines distantes vont reçevoir un mot et les fichiers dans lequels ont retrouve ce mot. Chaque machine va alors compter le nombre d'occurences de ce mot que l'on retrouve dans les fichiers, puis va retourner le résultat dans le fichier de résultat.


## 3.  Documentation Utilisateur

Plusieur fonctionnalités ont été implémenté pour personnaliser le logiciel, pour ce faire l'utilisateur doit modifier quelques paramètres dans la classe "Main" du package "local_shavadoop".

- Il est possible de modifier le nombre de lignes qui sont traitées par les Mappeurs, cela peut influer fortement sur les performances du logiciel, selon le nombre de machines disponibles et la taille du fichier en entrée. Pour se faire il faut modifier dans la méthode "main()": 
Main main = new Main(X); avec X le nombre le lignes à lire (entier positif)

- Pour choisir le fichier qui sera traité, il faut modifier dans la méthode main():
main.map_reduce_job(X);  avec X le chemin d'accès vers le fichier (une chaine de caractères String)

- Pour choisir le fichier dans lequel on écrira les résultats, il faut modifier dans la méthode map_reduce_job() ligne 156:
File f = new File(X);  avec X le chemin d'accès vers le fichier (une chaine de caractères String)

## 4.  Documentation Développeur

Le projet est divisé en plusieurs packages:
- local_shavadoop: le package qui comprend toutes les classes utilisées par le Master
- map_shavadoop: le package qui comprend toutes les classes utilisées par les slaves Mappers
- reduce_shavadoop: le package qui comprend toutes les classes utilisées par les slaves Reducers

### local_shavadoop
### map_shvadoop
### reduce_shavadoop

## 5.  Bugs / Améliorations possibles

Cette implementation de Map-Reduce:
- Gestion des fails
- Liste des PC
- Threads sur machines distantes
- Calcul multi coeurs
- Moins de lecture-écriture  -> Spark

## 6. Reflexion sur le paradigme

Le problème de l'architecture Map-Reduce est le goulot d'étranglement entre les étapes de Map et Reduce. En effet avant de lancer les Reducers, le Master doit attendre que le dictionnaire soit fini d'être rempli. Ainsi le temps d'execution de l'étape Map est égale au temps d'execution du plus long Mapper (sans tenir compte des éventuelles défaillances)

