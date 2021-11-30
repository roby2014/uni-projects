library ieee;
use ieee.std_logic_1164.all;


entity logic_or is
	port(	
		A: in std_logic_vector(3 downto 0);
		B: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
);
end logic_or;


architecture arq of logic_or is
begin
	O <= A or B;
end arq;