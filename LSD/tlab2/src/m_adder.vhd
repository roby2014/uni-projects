-- "somador" from lab4

library ieee;
use ieee.STD_LOGIC_1164.all;


entity m_adder is 
	port(
		A: in std_logic_vector(3 downto 0);
		B: in std_logic_vector(3 downto 0);
		Cin: in std_logic;
		
		S: out std_logic_vector(3 downto 0);
		Cout: out std_logic
	);
end m_adder;


architecture arq of m_adder is

component m_fulladder
	port(
		A, B, Cin: in std_logic;
		S, Cout: out std_logic
	);
end component;

signal c1, c2, c3: std_logic;

begin

-- fa 0
u_full_adder0: m_fulladder port map (
	A => A(0),
	B => B(0),
	Cin => Cin,
	S => S(0),
	Cout => c1
);
	
-- fa 1
u_full_adder1: m_fulladder port map (
	A => A(1),
	B => B(1),
	Cin => c1,
	S => S(1),
	Cout => c2
);
	
-- fa 2
u_full_adder2: m_fulladder port map (
	A => A(2),
	B => B(2),
	Cin => c2,
	S => S(2),
	Cout => c3
);

-- fa 3
u_full_adder3: m_fulladder port map (
	A => A(3),
	B => B(3),
	Cin => c3,
	S => S(3),
	Cout => Cout
);

end arq;
