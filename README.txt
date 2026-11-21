MOD PHOBIAS 0.9.0 - CODE SOURCE (lot 4)

  src/com/studiobing/phobias/Phobia.java        enum 82 entrées, TESTÉ
  src/com/studiobing/phobias/Masks.java         masques 2 longs (correctif 64 bits), TESTÉ en Java pur
  src/com/studiobing/phobias/ExtraPhobias4.java effets des 20 nouvelles phobies, NON COMPILÉ

Installation : copier src/ dans le projet du mod phobias (template Fabric 1.21.11),
ajouter les sources existantes du mod d origine, puis lancer ./gradlew build.

Les nouvelles phobies sont en implemented=false : elles ne sont pas tirées.
Ne passer à true qu après test en jeu et branchement de Masks dans le réseau.
Non testé en jeu.
