package model.jsfclases;

import com.indra.demo.model.RegistrarCalculo;
import model.jsfclases.util.JsfUtil;
import model.jsfclases.util.PaginationHelper;
import model.beans.RegistrarCalculoFacade;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.json.JsonArray;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;

@ManagedBean(name = "registrarCalculoController")
@SessionScoped
public class RegistrarCalculoController implements Serializable {

    private RegistrarCalculo current;
    private DataModel items = null;

    private WebTarget wgTarget;
    private Client client;
    private static String URL_BASE = "http://localhost:9898/Indra/registros";

    private PaginationHelper pagination;
    private int selectedItemIndex;

    public RegistrarCalculoController() {
        client = ClientBuilder.newClient();
        wgTarget = client.target(URL_BASE).path("guardar");
    }

    public RegistrarCalculo getSelected() {
        if (current == null) {
            current = new RegistrarCalculo();
            selectedItemIndex = -1;
        }
        return current;
    }
    /*
     public PaginationHelper getPagination() {
     if (pagination == null) {
     pagination = new PaginationHelper(10) {

     @Override
     public int getItemsCount() {
     return getFacade().count();
     }

     @Override
     public DataModel createPageDataModel() {
     return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
     }
     };
     }
     return pagination;
    

     public String prepareList() {
     recreateModel();
     return "List";
     }

     /* public String prepareView() {
     current = (RegistrarCalculo) getItems().getRowData();
     selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
     return "View";
     }*/
    private String el = "Límite debe ser mayor a 50 y menor a 500 o el nombre de usuario no es correcto";

    public String prepareCreate() {
        current = new RegistrarCalculo();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            
            if (current.getLimite() < 50 || current.getLimite() > 500 && (!current.getUsuario().equals("David")||!current.getUsuario().equals("Victoria") ||!current.getUsuario().equals("Juan")||!current.getUsuario().equals("Paulina")  )) {
                JsfUtil.addErrorMessage(el);
            } else {
                int res = limite(current.getLimite());
                current.setResultado(res);
                create(current);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("labelsform/Bundle").getString("RegistrarCalculoCreated"));
                return prepareCreate();
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("labelsform/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
        return null;
    }

    /*  public String prepareEdit() {
     current = (RegistrarCalculo) getItems().getRowData();
     selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
     return "Edit";
     }*/

    /*public String update() {
     try {
     edit(current);
     JsfUtil.addSuccessMessage(ResourceBundle.getBundle("labelsform/Bundle").getString("RegistrarCalculoUpdated"));
     return "View";
     } catch (Exception e) {
     JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("labelsform/Bundle").getString("PersistenceErrorOccured"));
     return null;
     }
     }*/

    /* public String destroy() {
     current = (RegistrarCalculo) getItems().getRowData();
     selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
     performDestroy();
     recreatePagination();
     recreateModel();
     return "List";
     }*/
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        // updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("labelsform/Bundle").getString("RegistrarCalculoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("labelsform/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    /* private void updateCurrentItem() {
     int count = getFacade().count();
     if (selectedItemIndex >= count) {
     // selected index cannot be bigger than number of items:
     selectedItemIndex = count - 1;
     // go to previous page if last page disappeared:
     if (pagination.getPageFirstItem() >= count) {
     pagination.previousPage();
     }
     }
     if (selectedItemIndex >= 0) {
     current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
     }
     }*/

    /*public DataModel getItems() {
     if (items == null) {
     items = getPagination().createPageDataModel();
     }
     return items;
     }*/
    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    /*public String next() {
     getPagination().nextPage();
     recreateModel();
     return "List";
     }

     public String previous() {
     getPagination().previousPage();
     recreateModel();
     return "List";
     }*/
    public SelectItem[] getItemsAvailableSelectMany() {
        WebTarget resource = wgTarget;
        String get = resource.request(MediaType.APPLICATION_JSON).get(String.class);
        return JsfUtil.getSelectItems(castToList(get), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        WebTarget resource = wgTarget;
        String get = resource.request(MediaType.APPLICATION_JSON).get(String.class);
        return JsfUtil.getSelectItems(castToList(get), false);
    }

    /* @FacesConverter(forClass = RegistrarCalculo.class)
     public static class RegistrarCalculoControllerConverter implements Converter {

     @Override
     public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
     if (value == null || value.length() == 0) {
     return null;
     }
     RegistrarCalculoController controller = (RegistrarCalculoController) facesContext.getApplication().getELResolver().
     getValue(facesContext.getELContext(), null, "registrarCalculoController");
     return controller.ejbFacade.find(getKey(value));
     }

     int getKey(String value) {
     int key;
     key = Integer.parseInt(value);
     return key;
     }

     String getStringKey(int value) {
     StringBuilder sb = new StringBuilder();
     sb.append(value);
     return sb.toString();
     }

     @Override
     public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
     if (object == null) {
     return null;
     }
     if (object instanceof RegistrarCalculo) {
     RegistrarCalculo o = (RegistrarCalculo) object;
     return getStringKey(o.getId());
     } else {
     throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RegistrarCalculo.class.getName());
     }
     }

     }*/
    private void remove(RegistrarCalculo entity) {
        String id = String.valueOf(entity.getId()) + "";
        WebTarget resource = wgTarget;
        resource.path(MessageFormat.format("{0}", new Object[]{id})).request().delete();
    }

    private void create(RegistrarCalculo entity) throws Exception {
        Response response = wgTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(entity, MediaType.APPLICATION_JSON), Response.class);

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new Exception("Error al crear la entidad" + response.getStatus());

        }
    }

    public List<RegistrarCalculo> castToList(String jsonArrayString) {
        JSONArray JsonArrayList = new JSONArray(jsonArrayString);
        List<RegistrarCalculo> list = new ArrayList<>();
        for (int i = 0; i < JsonArrayList.length(); i++) {
            RegistrarCalculo btr = new RegistrarCalculo(JsonArrayList.get(i).toString());
            list.add(btr);
        }
        return list;
    }

    public int limite(int n) {
        int sp = 0;
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                sp = sp + i;
            }
        }
        return sp;
    }
}
