LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
USE ieee.std_logic_arith.ALL;
USE ieee.std_logic_unsigned.all;

ENTITY multiplier_tb IS
END multiplier_tb;

ARCHITECTURE behavior OF multiplier_tb IS 

-- Component Declaration for the Unit Under Test (UUT)
COMPONENT tlab4 IS
 Port( 
		reset : in STD_LOGIC;
		Mclk : in std_logic;
		start: in std_logic;
		M, md : in std_logic_vector(3 downto 0);
		P : out std_logic_vector(6 downto 0);
		HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : out std_logic_vector(7 downto 0)
 );
END COMPONENT;

--Inputs
SIGNAL reset, Mclk, start :  std_logic := '0';
signal M, md : std_logic_vector(3 downto 0) := "0000";

--Outputs
signal P : std_logic_vector(6 downto 0) := "0000000";

signal HEX0, HEX1, HEX2, HEX3, HEX4, HEX5 : std_logic_vector(7 downto 0) := "00000000";


BEGIN

-- Instantiate the Unit Under Test (UUT)
uut: tlab4 PORT MAP(
	reset => reset,
	Mclk => Mclk,
	start => start,
	M => M,
	md => md,
	P => P,
	HEX0 => HEX0,
	HEX1 => HEX1,
	HEX2 => HEX2,
	HEX3 => HEX3,
	HEX4 => HEX4,
	HEX5 => HEX5
);

Mclk <= not Mclk after 3 ns;

tb : PROCESS
BEGIN

	 start <= '1'; -- change this
	 reset <= '1';
	wait for 20 ns;
	
	 reset <= '0';
	
	
	wait for 20 ns;
	M <= "1001";
	md <= "1001";
	
	wait for 20 ns;	
	
	start <= '0'; -- n this
	
	wait;

END PROCESS;

END;
