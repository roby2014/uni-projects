LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY verifica IS
	PORT (
		R : OUT STD_LOGIC; -- cancela
		D : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- departamento
		S : IN STD_LOGIC_VECTOR(1 DOWNTO 0) -- seletor
	);
END verifica;

-- abrir a cancela só e só se o departamento for igual ao seletor do mux
ARCHITECTURE arq_verifica OF verifica IS
BEGIN
	R <= ((NOT(S(0)) AND NOT(S(1)) AND D(0)) -- 00
		OR (NOT(S(0)) AND S(1) AND D(1)) -- 01
		OR (S(0) AND NOT(S(1)) AND D(2)) -- 10
		OR (S(0) AND S(1) AND D(3))); -- 11
END arq_verifica;