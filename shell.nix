{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk21
    leiningen
  ];
  JAVA_HOME="${pkgs.jdk21}/";
}
