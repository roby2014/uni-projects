library ieee;
use ieee.std_logic_1164.all;


entity logic_xor is
	port(	
		A: in std_logic_vector(3 downto 0);
		B: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
);
end logic_xor;


architecture arq of logic_xor is
begin
	O <= A xor B;
end arq;