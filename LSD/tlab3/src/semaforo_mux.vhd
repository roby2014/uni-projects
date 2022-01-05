-- mux do semaforo, escolhe se temos que contar TEMPO ou 2 segundos
-- F vai para o contador (lab 5)

LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY semaforo_mux IS
    PORT (
        Tempo : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
        S : IN STD_LOGIC;
        F : OUT STD_LOGIC_VECTOR(3 DOWNTO 0)
    );
END semaforo_mux;

ARCHITECTURE arq OF semaforo_mux IS
BEGIN
    F(0) <= (S AND Tempo(0)) OR (NOT(S) AND '0');
    F(1) <= (S AND Tempo(1)) OR (NOT(S) AND '1');
    F(2) <= (S AND Tempo(2)) OR (NOT(s) AND '0');
    F(3) <= (S AND Tempo(3)) OR (NOT(S) AND '0');
END arq;