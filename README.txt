PROJET PHOBIAS 0.9.0 - SQUELETTE FABRIC 1.21.11

Contenu :
  gradlew, build.gradle, gradle.properties, settings.gradle  -> template Fabric 1.21.11 (OK)
  src/main/java/com/studiobing/phobias/Phobia.java           -> enum 82 entrees (TESTE)
  src/main/java/com/studiobing/phobias/Masks.java            -> masques 2 longs (TESTE)
  src/main/java/com/studiobing/phobias/ExtraPhobias4.java    -> effets (NON COMPILE)
  src/main/resources/fabric.mod.json                         -> metadonnees du mod phobias 0.9.0
  .github/workflows/build.yml                                -> build GitHub Actions

MANQUE (a ajouter depuis le mod d origine, sinon le build echoue) :
  - toutes les autres classes Java du mod (PhobiasMod, Etat, RunState, Net, Phobias,
    ExtraPhobias, ExtraPhobias2, client/, mixin/, frog/, etc.)
  - les mixins (phobias.mixins.json) et les ressources (assets/, data/)
  - le fichier fabric.mod.json reference ces classes : il ne fonctionnera qu une fois
    le code complet present.
