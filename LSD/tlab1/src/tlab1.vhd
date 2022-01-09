LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY tlab1 IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- num funcionario
		R : OUT STD_LOGIC; -- cancela
		S : IN STD_LOGIC_VECTOR(1 DOWNTO 0) -- seletor
	);
END tlab1;

ARCHITECTURE main OF tlab1 IS

	-- identifica->verifica (bits departamento)
	SIGNAL x : STD_LOGIC_VECTOR(3 DOWNTO 0);

	-- identifica departamento
	COMPONENT identifica
		PORT (
			A : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- num funcionario
			D : OUT STD_LOGIC_VECTOR(3 DOWNTO 0) -- departamento
		);
	END COMPONENT;

	-- verifica se pertence ao departamento (MUX)
	COMPONENT verifica
		PORT (
			S : IN STD_LOGIC_VECTOR(1 DOWNTO 0); -- seletor
			D : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- departamento
			R : OUT STD_LOGIC --cancela
		);
	END COMPONENT;
BEGIN

	-- verifica dep
	u_identifica : identifica PORT MAP(
		A => A,
		D => x
	);

	-- mux
	u_verifica : verifica PORT MAP(
		S => S,
		D => x,
		R => R
	);

END main;