library ieee;
use ieee.STD_LOGIC_1164.all;


entity mux is 
	port(
		R: in std_logic_vector(3 downto 0);
		S: in std_logic_vector(3 downto 0);
		sel: in std_logic;
		
		F: out std_logic_vector(3 downto 0)
	);
end mux;


architecture arq of mux is
begin
F(0) <= ( sel and R(0)) or (not sel and S(0));
F(1) <= ( sel and R(1)) or (not sel and S(1));
F(2) <= ( sel and R(2)) or (not sel and S(2));
F(3) <= ( sel and R(3)) or (not sel and S(3));
end arq;