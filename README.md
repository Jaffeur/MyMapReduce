# Projet SHAVADOOP #
 
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

- Il faut modifier les répertoires où les machines distantes pourront trouver les .jar Map et Reduce à executer (via commande bash avec ssh)
ligne 146: 	String jar_path = X; avec X le chemin d'accès vers le fichier Split_Mapping.jar (une chaine de caractères String) 
ligne 171: 	String jar_path = X; avec X le chemin d'accès vers le fichier Reduce_Map.jar (une chaine de caractères String) 

## 4.  Documentation Développeur

Le projet est divisé en plusieurs packages:
- local_shavadoop: le package qui comprend toutes les classes utilisées par le Master
- map_shavadoop: le package qui comprend toutes les classes utilisées par les slaves Mappers
- reduce_shavadoop: le package qui comprend toutes les classes utilisées par les slaves Reducers

### 4.1 local_shavadoop

####- Classe Main
Il s'agit de la classe principale du Master.

- Methode main
Methode qui appelle toutes les methodes du job MapReduce. Elle appelle dans l'ordre les methodes suivantes.

- Methode getMachinesIP
Cette methode va aller chercher les machines disponibles dans le réseau avec l'utilisation de la commande nmap.
Les machines sont stockées dans une liste.

- Methode map_reduce_job
Cette methode est la méthode principale du Master elle va organiser les différentes étapes du job Map Reduce. 
Dans un premier temps elle va lire le fichier à traiter et le diviser en blocs de une ou plusieur lignes (selon les parametres définis). 

Pour chaque bloc on lance une tâche distribuée de type Map sur une autre machine contenue dans la liste avec la classe Parallelize qui hérite de la classe Thread.
Une fois toutes les tâches Map effectuées le dictionnaire est remplis avec la classe Dictionnaire. 

Ensuite pour chaque mot du dictionnaire on lance une tâche distribuée de type Reduce sur une autre machine contenue dans la liste avec la classe Parallelize. 

Rq: on affecte chaque nouvelle tâche Map ou Reduce à une machine différente de la liste.

- Methode word_sort
Methode utilisée pour trier les mots du fichier résultat en fonction du nombre d'occurence. Il faut parameter manuellement (dans le main) le fichier à trier.

####- Classe Parallelize
Cette classe va paralléliser les tâche Map et Réduce puisqu'elle hérite de Thread. Elle possède deux cosntructeur, un pour lancer une tâche Map et un pour lancer une tâche Reduce.

Un fois construite elle va communiquée avec la machine distance via ssh et executer une commande Bash sur la machine qui va elle même executer un .jar (selon qu'il s'agit d'un Map ou d'un Reduce).

####- Classe Dictionnaires
La classe dictionnaire est possède une methode qui va remplir le dictionnaire en lui passant en parametre un fichier de clé-valeur créer par un Mapper. Le dictionnaire contient une liste de clé-valeures, les clés sont les mots et les valeurs les différent fichiers dans lesquels ont les trouve.
Ce dictionnaire va servire de support pour répartir les fichiers à traiter dans les Reducers.

####- Classe Comparator
La classe comparator vient en soutient de la méthode word_sort pour comparer les mots un a un et les trier.

### 4.2 map_shvadoop
Ce package contient la classe Split_shavadoop qui doit être exportée en .jar pour être executée sur une machine distante. Elle réalise les tâches de Mapping:
Elle reçoit en parametres une ou plusieurs ligne de mots et un fichier dans lequel écrire les réponses.

Elle split les ligne de mots et pour chacun d'entre eux écrit dans le fichier de réponse une ligne sous forme cle-valeur. Avec le mot en clé et le nombre d'occurence du mot en valeur.

### 4.3 reduce_shavadoop
Ce package contient la classe Reduce_Map qui doit être exportée en .jar pour être executée sur une machine distante. Elle réalise les tâches de Reducing:
Elle reçoit en parametre le mot à dont on va compter le nombre d'occurence, la liste des fichiers dans lesquels on retrouve ce mot (grâce au dictionnaire), et le fichier de résultat.

Pour chaque fichier en entrée le Reducer va recherché le mot et comptabiliser sont nombre d'occurences total, puis va écrire le résultat sous forme d'une ligne clé-valeur dans le fichier.
Avec pour clé, le mot et pour valeur sont nombre d'occurences totales.

## 5.  Bugs / Améliorations possibles

- Gestion des pannes: il faudrait que les tâches distribuées qui n'aboutissent pas soient relancées sur une autre machine.
- Ordonnacement dans le cluster: en cas de panne ou de sur-utilisation de la mémoire d'une machine il faudrait distibuer les tâches sur les autres machines disponibles.
- En fonction du nombre de coeur des machines ditantes il faudrait dapter le nombre de tâches que l'on peut leur attribuer et aussi utiliser les threads.
- Ce logiciel pourrait être optimisé en utilisant moins de lecture écriture dans les fichier mais en utilisant uniquement la mémoire vive.
- Les échanges d'informations entre Master et Slaves gagneraient en efficacité avec l'utilisation de sockets, notament pour le push les données depuis les Mappers vers le Master pour remplir le dictionnaire.

## 6. Reflexion sur le paradigme

Le problème de l'architecture Map-Reduce est le goulot d'étranglement entre les étapes de Map et Reduce. En effet avant de lancer les Reducers, le Master doit attendre que le dictionnaire soit fini d'être rempli. Ainsi le temps d'execution de l'étape Map est égale au temps d'execution du plus long Mapper (sans tenir compte des éventuelles défaillances)

