library ieee;
use ieee.STD_LOGIC_1164.all;

entity identifica is 
	port(
		A: in std_logic_vector(3 downto 0); -- num funcionario
		D: out std_logic_vector(3 downto 0) -- departamento
	);
end identifica;

-- identificar o departamento do funcionario
architecture arq_identifica of identifica is
begin
	D(0) <= (	(not(A(1)) 	and A(2) 		and not(A(3)))
				or (A(0)			and not(A(1))	and not(A(3)))
				or (A(0) 		and A(1) 		and A(2)) 
				or (not(A(0)) 	and not(A(1)) 	and not(A(2)) and A(3)) 
				or (not(A(0)) 	and A(1) 		and not(A(2)) and not(A(3))));
	
	D(1) <= (	(not(A(0)) 	and not(A(2)) 	and not(A(3)))
				or (not(A(0)) 	and A(1) 		and not(A(3)) )
				or (A(1) 		and not(A(2)) 	and not(A(3)))
				or (A(0) 		and not(A(1)) 	and A(2)));
		
	D(2)  <= (	(A(0) 		and not(A(1)) and not(A(2))) 
				or (not(A(0)) 	and not(A(1)) and A(2)) 
				or (not(A(0)) 	and not(A(2)) and A(3)));
		
	D(3) <= (	(not(A(0)) 	and A(2) 		and A(3)) 
				or (not(A(1)) 	and A(2) 		and A(3))
				or (A(1) 		and A(2) 		and not(A(3)))
				or (A(0) 		and not(A(2)) 	and A(3))
				or (not(A(0)) 	and not(A(1)) 	and not(A(2)) and not(A(3))));
end arq_identifica;
	