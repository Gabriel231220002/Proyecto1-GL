package com.example.Adaptadores.Salida;

import com.example.Dominio.ItemInventario;
import com.example.Puertos.IItemInventarioRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresItemInventarioRepository implements IItemInventarioRepository {

    @Override
    public void guardar(ItemInventario item) throws Exception {
        String sql = "INSERT INTO item_inventario (labarotario_id, codigo, nombre, descripcion, estado_fisico, foto) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getLaboratorioId());
            ps.setString(2, item.getCodigo());
            ps.setString(3, item.getNombre());
            ps.setString(4, item.getDescripcion());
            ps.setString(5, item.getEstadoFisico() != null ? item.getEstadoFisico() : "BUENO");
            ps.setString(6, item.getFoto());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(ItemInventario item) throws Exception {
        String sql = "UPDATE item_inventario SET labarotario_id = ?, codigo = ?, nombre = ?, descripcion = ?, "
                + "estado_fisico = ?, foto = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getLaboratorioId());
            ps.setString(2, item.getCodigo());
            ps.setString(3, item.getNombre());
            ps.setString(4, item.getDescripcion());
            ps.setString(5, item.getEstadoFisico());
            ps.setString(6, item.getFoto());
            ps.setInt(7, item.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM item_inventario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public ItemInventario obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM item_inventario WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearItem(rs);
                }
            }
        }
        return null;
    }

    @Override
    public ItemInventario obtenerPorCodigo(String codigo) throws Exception {
        String sql = "SELECT * FROM item_inventario WHERE UPPER(codigo) = UPPER(?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearItem(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ItemInventario> listar() throws Exception {
        List<ItemInventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM item_inventario ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearItem(rs));
            }
        }
        return lista;
    }

    @Override
    public List<ItemInventario> listarPorLaboratorio(int laboratorioId) throws Exception {
        List<ItemInventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM item_inventario WHERE labarotario_id = ? ORDER BY id ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, laboratorioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearItem(rs));
                }
            }
        }
        return lista;
    }

    private ItemInventario mapearItem(ResultSet rs) throws SQLException {
        return new ItemInventario(
            rs.getInt("id"),
            rs.getInt("labarotario_id"),
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getString("estado_fisico"),
            rs.getString("foto")
        );
    }
}
