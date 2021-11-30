library ieee;
use ieee.STD_LOGIC_1164.all;

entity tlab1 is 
	port(
		A: in std_logic_vector(3 downto 0); -- num funcionario
		R: out std_logic; 						-- cancela
		S: in std_logic_vector(1 downto 0) 	-- seletor
	);
end tlab1;

architecture main of tlab1 is

-- identifica->verifica (bits departamento)
signal x: std_logic_vector(3 downto 0);

-- identifica departamento
component identifica
	port(
		A : in std_logic_vector(3 downto 0); 	-- num funcionario
		D: out std_logic_vector(3 downto 0) 	-- departamento
	);
end component;

-- verifica se pertence ao departamento (MUX)
component verifica
	port(
		S: in std_logic_vector(1 downto 0); -- seletor
		D: in std_logic_vector(3 downto 0); -- departamento
		R: out std_logic 							--cancela
	);
end component;


begin

-- verifica dep
u_identifica: identifica port map (
	A => A,
	D => x
);

-- mux
u_verifica: verifica port map (
	S => S,
	D => x,
	R => R
);
		
end main;
	