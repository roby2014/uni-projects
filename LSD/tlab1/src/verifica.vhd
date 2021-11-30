library ieee;
use ieee.STD_LOGIC_1164.all;

entity verifica is 
	port(
		R: out std_logic; 						-- cancela
		D: in std_logic_vector(3 downto 0); -- departamento
		S: in std_logic_vector(1 downto 0) 	-- seletor
	);
end verifica;

-- abrir a cancela só e só se o departamento for igual ao seletor do mux
architecture arq_verifica of verifica is
begin
	R <=  ((not(S(0)) and not(S(1)) 	and D(0)) 	-- 00
		or (not(S(0)) 	and S(1) 		and D(1))	-- 01
		or (S(0) 		and not(S(1)) 	and D(2)) 	-- 10
		or (S(0) 		and S(1) 		and D(3)));	-- 11
end arq_verifica;
	