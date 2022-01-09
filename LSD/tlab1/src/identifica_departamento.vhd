LIBRARY ieee;
USE ieee.STD_LOGIC_1164.ALL;

ENTITY identifica IS
	PORT (
		A : IN STD_LOGIC_VECTOR(3 DOWNTO 0); -- num funcionario
		D : OUT STD_LOGIC_VECTOR(3 DOWNTO 0) -- departamento
	);
END identifica;

-- identificar o departamento do funcionario
ARCHITECTURE arq_identifica OF identifica IS
BEGIN
	D(0) <= ((NOT(A(1)) AND A(2) AND NOT(A(3)))
	OR (A(0) AND NOT(A(1)) AND NOT(A(3)))
	OR (A(0) AND A(1) AND A(2))
	OR (NOT(A(0)) AND NOT(A(1)) AND NOT(A(2)) AND A(3))
	OR (NOT(A(0)) AND A(1) AND NOT(A(2)) AND NOT(A(3))));

	D(1) <= ((NOT(A(0)) AND NOT(A(2)) AND NOT(A(3)))
	OR (NOT(A(0)) AND A(1) AND NOT(A(3)))
	OR (A(1) AND NOT(A(2)) AND NOT(A(3)))
	OR (A(0) AND NOT(A(1)) AND A(2)));

	D(2) <= ((A(0) AND NOT(A(1)) AND NOT(A(2)))
	OR (NOT(A(0)) AND NOT(A(1)) AND A(2))
	OR (NOT(A(0)) AND NOT(A(2)) AND A(3)));

	D(3) <= ((NOT(A(0)) AND A(2) AND A(3))
	OR (NOT(A(1)) AND A(2) AND A(3))
	OR (A(1) AND A(2) AND NOT(A(3)))
	OR (A(0) AND NOT(A(2)) AND A(3))
	OR (NOT(A(0)) AND NOT(A(1)) AND NOT(A(2)) AND NOT(A(3))));
END arq_identifica;