package construct.exception;

public class FieldError extends RuntimeException{
  public FieldError( String msg )
  {
    super(msg);
  }
  
  public FieldError( Exception e )
  {
    super(e);
  }
  
}
