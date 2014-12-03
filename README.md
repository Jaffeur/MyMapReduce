 ## Projet SHAVADOOP
Implementation de Map Reduce en Java

 

## 1.  Introduction


Dans le cadre de ce projet j'ai implémenté un programme qui suit l'architecture Map Reduce documenté par Google. Ce programme est codé en Java et test un « word-count » sur un document donné et retourne un fichier avec le décompte des mots.

Dans l'architecture de ce programme il y a un Master qui dirige le job Map-Reduce et les Slaves qui executes des tâches qui peuvent être distribuées sur d'autre machines distantes.

Le master lit et découpe - selon un nombre de lignes données - le texte en entré, et envoi de manière distribuée les lignes aux Slaves qui vont "Spliter" les données et les écrire dans un fichier sous forme Clé(mot)-Valeur(nombre d'occurence de mots). Cette étape est le Map.
A la fin de chaque "Split" le Master lit le fichier et construit un dictionnaire qui va referencer les mots et et fichiers dans lesquels ils se trouvent. Clé(mot) - Valeur (adresse du fichier)

Quand tous les Map sont finis, pour chaque mot dans le dictionnaire le Master va lancer un Reduce sur une machine distante: les machines distantes vont reçevoir 

## 3.  Documentation Utilisateur

## 4.  Documentation Développeur

## 5.  Bugs / Améliorations possibles

Cette implementation de Map-Reduce

## 6. Reflexion sur le paradigme

Le problème de l'architecture Map-Reduce est le goulot d'étranglement entre les étapes de Map et Reduce. En effet avant de lancer les Reducers, le Master doit attendre que le dictionnaire soit fini d'être rempli. Ainsi le temps d'execution de l'étape Map est égale au temps d'execution du plus long Mapper (sans tenir compte des éventuelles défaillances)
