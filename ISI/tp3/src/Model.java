import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;

/// Model class, provides wrapper functions to manipulate the database.
public class Model {
    private static Model __instance = null;
    private Connection _connection = null;
    private String __connectionString = null;

    /// Singleton.
    public static Model getInstance() {
        if (__instance == null) {
            __instance = new Model();
        }
        return __instance;
    }

    /// Returns connection string.
    public String getConnectionString() {
        return __connectionString;
    }

    /// Set connection string to [s].
    public void setConnectionString(String s) {
        __connectionString = s;
    }

    /// Returns a database connection, depending on [__connectionString]
    public Connection connect() {
        try {
            return DriverManager.getConnection(__connectionString);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void disconnect() {
        try {
            _connection.close();
        } catch (SQLException e) {
        }
    }

    /// Returns the distance between 2 points in kilometers
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return (dist);
        }
    }

    /// Inserts a new driver
    public void insertDriver(int idpessoa, String nccconducao, LocalDate birthday) {
        try {
            // Connect to database
            _connection = connect();

            // Check if driver is not proprietary
            PreparedStatement proprietarioStmt = _connection
                    .prepareStatement("SELECT idpessoa FROM PROPRIETARIO WHERE idpessoa = ?");
            proprietarioStmt.setInt(1, idpessoa);
            ResultSet proprietarioRs = proprietarioStmt.executeQuery();
            if (proprietarioRs.next()) {
                throw new IllegalArgumentException("Driver can not be a proprietary");
            }

            // Check if the driver is at least 18 years old
            LocalDate minimumBirthday = LocalDate.now().minus(Period.ofYears(18));
            if (birthday.isAfter(minimumBirthday)) {
                throw new IllegalArgumentException("Driver must be at least 18 years old");
            }

            // Create a prepared statement
            String query = "INSERT INTO condutor (idpessoa, ncconducao, dtnascimento) VALUES (?, ?, ?)";
            PreparedStatement preparedStmt = _connection.prepareStatement(query);

            // Set values for the prepared statement (automatically escaped to prevent
            // injection)
            preparedStmt.setInt(1, idpessoa);
            preparedStmt.setString(2, nccconducao);
            preparedStmt.setDate(3, java.sql.Date.valueOf(birthday));

            // Execute the prepared statement
            preparedStmt.executeUpdate();

            // Close the connection
            _connection.close();
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void insertOldVehicle(String matricula) {
        try {
            // Connect to database
            _connection = connect();

            // Create old vehicle table
            try {
                String veiculoOldQuery = """
                            CREATE table if not exists VEICULO_OLD(
                            id integer primary key,
                            matricula varchar(10) not null,
                            tipo integer not null,
                            modelo varchar(10) not null,
                            marca varchar(10) not null,
                            ano integer CHECK (ano + 5 <= date_part('year', now())),
                            proprietario integer not null,
                            numViagens integer not null,
                            kilometrosTotais integer not null,
                            foreign key (tipo) references TIPOVEICULO(tipo),
                            foreign key (proprietario) references PROPRIETARIO(idpessoa)
                        )""";
                Statement veiculoOldStmt = _connection.createStatement();
                veiculoOldStmt.executeQuery(veiculoOldQuery);
            } catch (org.postgresql.util.PSQLException e) {
                // nothing bad, it probably already exists
            }

            // Select vehicle by matricula
            PreparedStatement veiculoStmt = _connection.prepareStatement("SELECT * FROM veiculo WHERE matricula = ?");
            veiculoStmt.setString(1, matricula);
            ResultSet veiculoRs = veiculoStmt.executeQuery();
            if (!veiculoRs.next()) {
                throw new IllegalArgumentException("Matricula not valid");
            }

            int id = veiculoRs.getInt("id");
            String matriculaValue = veiculoRs.getString("matricula");
            int tipo = veiculoRs.getInt("tipo");
            String modelo = veiculoRs.getString("modelo");
            String marca = veiculoRs.getString("marca");
            int ano = veiculoRs.getInt("ano");
            int proprietario = veiculoRs.getInt("proprietario");

            // Select viagem count by vehicle id
            PreparedStatement veiculoNViagensStmt = _connection.prepareStatement(
                    """
                                        SELECT v.id, count(v2.idsistema) NumViagens
                                        FROM proprietario p join pessoa p2 on p.dtnascimento notnull and p2.nif notnull and p.idpessoa = p2.id
                                                join veiculo v on v.proprietario = p.idpessoa and v.matricula = ?
                                                join viagem v2 on v2.veiculo = v.id
                                        group by v.id
                            """);
            veiculoNViagensStmt.setString(1, matricula);
            ResultSet veiculoNViagensRs = veiculoNViagensStmt.executeQuery();
            if (!veiculoNViagensRs.next()) {
                throw new IllegalArgumentException("Could not fetch NumViagens");
            }

            int nViagens = veiculoNViagensRs.getInt("NumViagens");

            // Insert into new table
            PreparedStatement veiculoOldInsertStmt = _connection.prepareStatement(
                    """
                                        INSERT INTO veiculo_old (id, matricula, tipo, modelo, marca, ano,
                                        proprietario, numviagens, kilometrostotais) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """);

            PreparedStatement veiculoViagensStmt = _connection.prepareStatement("""
                    SELECT idsistema, latinicio, latfim, longinicio, longfim FROM viagem WHERE veiculo = ?
                    """);
            veiculoViagensStmt.setInt(1, id);
            ResultSet veiculoViagensRs = veiculoViagensStmt.executeQuery();

            List<Double> latIniValues = new ArrayList<>();
            List<Double> latFimValues = new ArrayList<>();
            List<Double> longIniValues = new ArrayList<>();
            List<Double> longFimValues = new ArrayList<>();

            int idSistema = -1;
            boolean first = false;
            while (veiculoViagensRs.next()) {
                if (!first) {
                    idSistema = veiculoViagensRs.getInt("idsistema");
                }
                first = true;
                latIniValues.add(veiculoViagensRs.getDouble("latinicio"));
                latFimValues.add(veiculoViagensRs.getDouble("latfim"));
                longIniValues.add(veiculoViagensRs.getDouble("longinicio"));
                longFimValues.add(veiculoViagensRs.getDouble("longfim"));
            }

            if (idSistema == -1) {
                throw new IllegalArgumentException("Could not fetch idSistema");
            }

            // Get the total distance of the veiculo
            double totalDistance = 0;
            for (int i = 0; i < latIniValues.size(); i++) {
                double lat1 = latIniValues.get(i);
                double lat2 = latFimValues.get(i);
                double lon1 = longIniValues.get(i);
                double lon2 = longFimValues.get(i);
                totalDistance += distance(lat1, lon1, lat2, lon2);
            }

            // Set the values for the INSERT statement
            veiculoOldInsertStmt.setInt(1, id);
            veiculoOldInsertStmt.setString(2, matriculaValue);
            veiculoOldInsertStmt.setInt(3, tipo);
            veiculoOldInsertStmt.setString(4, modelo);
            veiculoOldInsertStmt.setString(5, marca);
            veiculoOldInsertStmt.setDouble(6, ano);
            veiculoOldInsertStmt.setInt(7, proprietario);
            veiculoOldInsertStmt.setInt(8, nViagens);
            veiculoOldInsertStmt.setInt(9, Math.toIntExact(Math.round(totalDistance)));

            // Execute the INSERT statement
            veiculoOldInsertStmt.executeUpdate();

            // Delete from original table
            PreparedStatement clienteViagemDeleteStmt = _connection
                    .prepareStatement("DELETE FROM clienteviagem WHERE viagem = ?");
            clienteViagemDeleteStmt.setInt(1, idSistema);
            clienteViagemDeleteStmt.executeUpdate();

            // Delete from original table
            PreparedStatement viagemDeleteStmt = _connection
                    .prepareStatement("DELETE FROM viagem WHERE veiculo = ?");
            viagemDeleteStmt.setInt(1, id);
            viagemDeleteStmt.executeUpdate();

            // Delete from original table
            PreparedStatement periodoAtivoDeleteStmt = _connection
                    .prepareStatement("DELETE FROM periodoactivo WHERE veiculo = ?");
            periodoAtivoDeleteStmt.setInt(1, id);
            periodoAtivoDeleteStmt.executeUpdate();

            // Delete from original table
            PreparedStatement corVeiculoDeleteStmt = _connection
                    .prepareStatement("DELETE FROM corveiculo WHERE veiculo = ?");
            corVeiculoDeleteStmt.setInt(1, id);
            corVeiculoDeleteStmt.executeUpdate();

            // Delete from original table
            PreparedStatement veiculoDeleteStmt = _connection
                    .prepareStatement("DELETE FROM veiculo WHERE matricula = ?");
            veiculoDeleteStmt.setString(1, matricula);
            veiculoDeleteStmt.executeUpdate();

            /// Close connection
            _connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getVehicles() {
        try {
            // Connect to database
            _connection = connect();

            // Create statement
            String query = "SELECT * FROM VEICULO";
            Statement st = _connection.createStatement();

            // Get result set
            ResultSet rs = st.executeQuery(query);

            // Return result set
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void displayVehicleInfo(String matricula) {
        try {
            // Connect to database
            _connection = connect();

            // Select vehicle by matricula
            PreparedStatement veiculoStmt = _connection.prepareStatement("SELECT * FROM veiculo WHERE matricula = ?");
            veiculoStmt.setString(1, matricula);
            ResultSet veiculoRs = veiculoStmt.executeQuery();
            if (!veiculoRs.next()) {
                throw new IllegalArgumentException("Matricula not valid");
            }

            int id = veiculoRs.getInt("id");

            PreparedStatement veiculoViagensStmt = _connection.prepareStatement("""
                    SELECT idsistema, valfinal, latinicio, latfim, longinicio, longfim FROM viagem WHERE veiculo = ?
                    """);
            veiculoViagensStmt.setInt(1, id);
            ResultSet veiculoViagensRs = veiculoViagensStmt.executeQuery();

            PreparedStatement periodoAtivoViagensStmt = _connection.prepareStatement("""
                    SELECT dtinicio, dtfim FROM periodoactivo WHERE veiculo = ?
                    """);
            periodoAtivoViagensStmt.setInt(1, id);
            ResultSet periodoAtivoViagensRs = periodoAtivoViagensStmt.executeQuery();

            List<Double> latIniValues = new ArrayList<>();
            List<Double> latFimValues = new ArrayList<>();
            List<Double> longIniValues = new ArrayList<>();
            List<Double> longFimValues = new ArrayList<>();
            List<Double> valFinalValues = new ArrayList<>();
            List<Timestamp> dtInicioValues = new ArrayList<>();
            List<Timestamp> dtFimValues = new ArrayList<>();

            int idSistema = -1;
            boolean first = false;
            while (veiculoViagensRs.next()) {
                if (!first) {
                    idSistema = veiculoViagensRs.getInt("idsistema");
                }
                first = true;
                latIniValues.add(veiculoViagensRs.getDouble("latinicio"));
                latFimValues.add(veiculoViagensRs.getDouble("latfim"));
                longIniValues.add(veiculoViagensRs.getDouble("longinicio"));
                longFimValues.add(veiculoViagensRs.getDouble("longfim"));
                valFinalValues.add(veiculoViagensRs.getDouble("valfinal"));
            }

            while (periodoAtivoViagensRs.next()) {
                dtInicioValues.add(periodoAtivoViagensRs.getTimestamp("dtinicio"));
                dtFimValues.add(periodoAtivoViagensRs.getTimestamp("dtfim"));
            }

            if (idSistema == -1) {
                throw new IllegalArgumentException("Could not fetch idSistema");
            }

            // Get the values
            double totalDistance = 0;
            double valueFinal = 0;
            long totalHours = 0;
            for (int i = 0; i < latIniValues.size(); i++) {
                double lat1 = latIniValues.get(i);
                double lat2 = latFimValues.get(i);
                double lon1 = longIniValues.get(i);
                double lon2 = longFimValues.get(i);
                Timestamp dtInicio = dtInicioValues.get(i);
                Timestamp dtFim = dtFimValues.get(i);
                totalDistance += distance(lat1, lon1, lat2, lon2);
                valueFinal += valFinalValues.get(i);
                totalHours += ChronoUnit.HOURS.between(dtInicio.toInstant(), dtFim.toInstant());
            }
            System.out.println("Total Hours: " + totalHours + " Total Distance: "
                    + Math.toIntExact(Math.round(totalDistance)) + " Total Cost: " + valueFinal);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet displayDriversMostTrips(int year) {
        try {
            // Connect to database
            _connection = connect();

            // Create prepared statement
            String query = """
                    select v.condutor as id, p.nproprio, p.apelido, p.nif, count(condutor) totalviagens
                    from viagem v join pessoa p on v.condutor = p.id and extract(year from v.dtviagem) = ?
                    group by v.condutor, p.nproprio, p.apelido, p.nif
                        having count (v.condutor) = (
                     select max(totalViagensCount)
                     from(
                     	select v2.condutor, count(v2.condutor) totalViagensCount
                     	from viagem v2
                     	group by v2.condutor) as A
                     )
                         order by id;
                     """;
            PreparedStatement preparedStatement = _connection.prepareStatement(query);
            preparedStatement.setInt(1, year);

            // Get result set
            ResultSet rs = preparedStatement.executeQuery();

            // Return result set
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet displayDriversNoTrips() {
        try {
            // Connect to database
            _connection = connect();

            // Create statement
            String query = """
                    select p.id, p.nproprio, p.apelido, p.nif
                    from (
                     select c.idpessoa
                     from condutor c except select v.condutor from viagem v
                    ) as A
                    inner join pessoa p on A.idpessoa = p.id
                    """;
            Statement st = _connection.createStatement();

            // Get result set
            ResultSet rs = st.executeQuery(query);

            // Return result set
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet displayNrOfTrips(String nif, int year) {
        try {
            // Connect to database
            _connection = connect();

            // Create statement
            String query = """
                    select v.id, count(v2.idsistema) NumViagens
                    from proprietario p join pessoa p2 on p.dtnascimento notnull and p2.nif = ? and p.idpessoa = p2.id
                        join veiculo v on v.proprietario = p.idpessoa
                        join viagem v2 on v2.veiculo = v.id and extract(year from v2.dtviagem) = ?
                    group by v.id;
                        """;
            PreparedStatement preparedStatement = _connection.prepareStatement(query);

            // Set values for the prepared statement (automatically escaped to prevent
            // injection)
            preparedStatement.setString(1, nif);
            preparedStatement.setInt(2, year);

            // Execute the prepared statement
            ResultSet rs = preparedStatement.executeQuery();

            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet displayInfoOfDriver(int year) {
        try {
            // Connect to database
            _connection = connect();

            // Create prepared statement
            String query = """
                                   select distinct  p.nproprio, p.apelido, p.noident, p.morada, sum(v.valfinal) TotalValFinal
                                   from condutor c join pessoa p on c.dtnascimento notnull and p.atrdisc = 'C' and c.idpessoa = p.id
                                   join viagem v on v.condutor = c.idpessoa and extract(year from v.dtviagem) = ?
                                   group by p.nproprio, p.apelido, p.noident, p.morada
                                   order by TotalValFinal desc
                    limit 1;
                                   """;
            PreparedStatement preparedStatement = _connection.prepareStatement(query);

            // Set values for the prepared statement (automatically escaped to prevent
            // injection)
            preparedStatement.setInt(1, year);

            // Execute the prepared statement
            ResultSet rs = preparedStatement.executeQuery();

            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}