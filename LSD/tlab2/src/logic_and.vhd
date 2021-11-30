library ieee;
use ieee.std_logic_1164.all;


entity logic_and is
	port(	
		A: in std_logic_vector(3 downto 0);
		B: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
);
end logic_and;


architecture arq of logic_and is
begin
	O <= A and B;
end arq;