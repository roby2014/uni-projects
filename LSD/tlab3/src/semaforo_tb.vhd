LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY tlab3_tb IS
END tlab3_tb;
 
ARCHITECTURE behavior OF tlab3_tb IS 
 
    -- Component Declaration for the Unit Under Test (UUT)
 
    COMPONENT tlab3 IS
	PORT(	clk : in std_logic;
		reset : in std_logic;
		B : IN STD_LOGIC;
		Tempo : IN std_logic_VECTOR(3 downto 0);
		E, A, V, EP, VP : out std_logic;
		HEX0 : out std_logic_vector(7 downto 0);
		Q : out std_logic_VECTOR(3 downto 0)
		);
	END COMPONENT;
    

    --Inputs
    signal clk, reset, B : std_logic := '0';
   	signal Tempo : std_logic_vector(3 downto 0) := "0000";

 	--Outputs
	signal Q : std_logic_VECTOR(3 downto 0);
	signal E, A, V, EP, VP : std_logic;
	signal HEX0 : std_logic_vector(7 downto 0);

   -- Clock period definitions
   constant clk_period : time := 50000000 ns;
 
BEGIN
 
	-- Instantiate the Unit Under Test (UUT)
   uut: tlab3 PORT MAP (
          clk => clk,
		  reset => reset,
          B => B,
		  Tempo => Tempo,
          Q => Q,
          E => E,
		  A => A,
		  V => V,
		  EP => EP,
          VP => VP,
		  HEX0 => HEX0
        );

	clk <= not clk after 10 ns; 

   -- Stimulus process
   stim_proc: process
   begin		
		
		wait for 20 ns;
		reset <= '1';
		Tempo <= "0100";
		B <= '0';
		
		wait for 20 ns;
		reset <= '0';
		B <= '0';
		
		wait for 100 ns;

		reset <= '0';
		B <= '1';

		wait for 100 ns;
		B <= '0';

		wait;
		
   end process;

END;
