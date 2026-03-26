package com.irctc.servlet;

import com.irctc.dao.DBConnection;
import com.irctc.model.FoodItem;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalTime;

@WebServlet("/FoodOrderingServlet")
public class FoodOrderingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String pnr = request.getParameter("pnr");
        String viewType = request.getParameter("view"); 
        
        String mealCategory = "ALL_DAY";

        if ("full".equals(viewType)) {
            mealCategory = "FULL_MENU";
        } else {
            LocalTime now = LocalTime.now();
            if (now.isAfter(LocalTime.of(5, 0)) && now.isBefore(LocalTime.of(11, 0))) {
                mealCategory = "BREAKFAST";
            } else if (now.isAfter(LocalTime.of(11, 0)) && now.isBefore(LocalTime.of(16, 0))) {
                mealCategory = "LUNCH";
            } else if (now.isAfter(LocalTime.of(18, 0)) && now.isBefore(LocalTime.of(23, 0))) {
                mealCategory = "DINNER";
            }
        }

        List<FoodItem> menu = getMenuFromDB(mealCategory);
        
        request.setAttribute("menuList", menu);
        request.setAttribute("mealCategory", mealCategory);
        request.setAttribute("pnr", pnr);

        request.getRequestDispatcher("jsp/foodOrdering.jsp").forward(request, response);
    }

    private List<FoodItem> getMenuFromDB(String category) {
        List<FoodItem> list = new ArrayList<>();
        Set<String> addedItems = new HashSet<>(); // To track duplicates
        String sql;
        
        // FIX: Removed 'GROUP BY' from SQL to prevent database errors.
        // We will fetch all and filter duplicates in Java instead.
        if ("FULL_MENU".equals(category)) {
            sql = "SELECT * FROM food_menu ORDER BY category";
        } else {
            sql = "SELECT * FROM food_menu WHERE category = ? OR category = 'ALL_DAY' OR category = 'SNACKS'";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (!"FULL_MENU".equals(category)) {
                if("DINNER".equals(category)) category = "LUNCH"; 
                ps.setString(1, category);
            }
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String name = rs.getString("item_name");
                
                // JAVA DEDUPLICATION:
                // If we are in FULL_MENU mode, and we've already seen this item name, skip it.
                if ("FULL_MENU".equals(category) && addedItems.contains(name)) {
                    continue; 
                }
                addedItems.add(name);

                FoodItem item = new FoodItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(name);
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getDouble("price"));
                item.setType(rs.getString("type"));
                
                String cat = rs.getString("category");
                // Merge labels for cleaner UI
                if ("LUNCH".equals(cat) || "DINNER".equals(cat)) {
                    item.setCategory("MEALS");
                } else {
                    item.setCategory(cat);
                }
                
                item.setImageUrl(rs.getString("image_url"));
                list.add(item);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}