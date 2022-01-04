LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;

ENTITY lab5_tb IS
END ENTITY;

ARCHITECTURE lab5_tb_arch OF lab5_tb IS

	COMPONENT lab5
		PORT (
			Data_in : IN STD_LOGIC_VECTOR(3 DOWNTO 0);
			CLK, PL, CE, clear : IN STD_LOGIC;
			Q : OUT STD_LOGIC_VECTOR(3 DOWNTO 0);
			SEG7 : OUT STD_LOGIC_VECTOR(7 DOWNTO 0); -- dpgfedcba
			TC : OUT STD_LOGIC);
	END COMPONENT;

	-- UUT signals
	SIGNAL CLK_TB : STD_LOGIC := '0';
	SIGNAL PL_TB, CE_TB, TC_TB, CLR_TB : STD_LOGIC;
	SIGNAL DATA_IN_TB, Q_TB : STD_LOGIC_VECTOR(3 DOWNTO 0);
	SIGNAL SEG7_TB : STD_LOGIC_VECTOR(7 DOWNTO 0);
	CONSTANT MCLK_PERIOD : TIME := 20 ns;
	CONSTANT MCLK_HALF_PERIOD : TIME := MCLK_PERIOD / 2;
	CONSTANT CLK_PERIOD : TIME := 20 ns;

BEGIN

	CLK_TB <= NOT CLK_TB AFTER MCLK_HALF_PERIOD;
	ULAB5 : lab5 PORT MAP(Data_in => DATA_IN_TB, CLK => CLK_TB, PL => PL_TB, CE => CE_TB, clear => CLR_TB, Q => Q_TB, SEG7 => SEG7_TB, TC => TC_TB);

	stimulus : PROCESS
	BEGIN
		-- Carrega contador com o valor 4
		CE_TB <= '1';
		PL_TB <= '1';
		DATA_IN_TB <= "0100";
		CLR_TB <= '0';
		WAIT FOR CLK_PERIOD;
		-- Decrementa o contador até dar uma volta completa (termina no valor 4)
		-- Durante a contagem, ao fim de 4 ciclos de clock, deve passar por TC
		PL_TB <= '0';
		WAIT FOR CLK_PERIOD * 16;
		-- Enibe o contador de contar durante dois períodos de clock
		CE_TB <= '0';
		WAIT FOR CLK_PERIOD * 2;
		-- Decrementa até atingir TC
		CE_TB <= '1';
		WAIT FOR CLK_PERIOD * 4;
		-- Carrega contador com o valor 4 em situação de TC
		PL_TB <= '1';
		WAIT FOR CLK_PERIOD;
		-- Faz clear ao display de 7 segmentos
		PL_TB <= '0';
		CLR_TB <= '1';

		WAIT;
	END PROCESS;

END ARCHITECTURE;