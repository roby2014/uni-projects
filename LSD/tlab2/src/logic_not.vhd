library ieee;
use ieee.std_logic_1164.all;


entity logic_not is
	port(	
		A: in std_logic_vector(3 downto 0);
		O: out std_logic_vector(3 downto 0)
);
end logic_not;


architecture arq of logic_not is
begin
	O <= not(A);
end arq;