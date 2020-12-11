package testjavarpc.http;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Config
 */
public class ServiceRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceRegistry() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String interf = request.getParameter(HttpRpcServer.INTERFACE_FULL_NAME);
		String impl = request.getParameter(HttpRpcServer.CLASS_FULL_NAME); 
		try {
			HttpRpcServer.serviceRegistry.put(interf, Class.forName(impl));
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}
		doGet(request, response);
	}

}
