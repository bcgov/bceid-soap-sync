{
  description = "A flake for developing bceid-soap-sync";

  inputs = {
    nixpkgs.url = github:NixOS/nixpkgs;
  };

  outputs = { self, nixpkgs }:
  let
    pkgs = nixpkgs.legacyPackages.x86_64-linux;
    system = "x86_64-linux";
  in {
    devShell.${system} = pkgs.mkShell {
      buildInputs = with pkgs; [
        jdk21
        leiningen
      ];
      JAVA_HOME="${pkgs.jdk21}";
    };
  };
}
