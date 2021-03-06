package org.deltaj.generator;

import com.google.inject.Inject;
import java.util.Arrays;
import java.util.List;
import org.deltaj.deltaj.AndOrExpression;
import org.deltaj.deltaj.ArithmeticSigned;
import org.deltaj.deltaj.Assignment;
import org.deltaj.deltaj.BasicType;
import org.deltaj.deltaj.BoolConstant;
import org.deltaj.deltaj.BooleanNegation;
import org.deltaj.deltaj.Cast;
import org.deltaj.deltaj.ClassType;
import org.deltaj.deltaj.Comparison;
import org.deltaj.deltaj.Expression;
import org.deltaj.deltaj.ExpressionStatement;
import org.deltaj.deltaj.Field;
import org.deltaj.deltaj.FieldSelection;
import org.deltaj.deltaj.IfStatement;
import org.deltaj.deltaj.IntConstant;
import org.deltaj.deltaj.JavaVerbatim;
import org.deltaj.deltaj.LocalVariableDeclaration;
import org.deltaj.deltaj.Message;
import org.deltaj.deltaj.Method;
import org.deltaj.deltaj.MethodCall;
import org.deltaj.deltaj.Minus;
import org.deltaj.deltaj.MultiOrDiv;
import org.deltaj.deltaj.New;
import org.deltaj.deltaj.Null;
import org.deltaj.deltaj.Original;
import org.deltaj.deltaj.Parameter;
import org.deltaj.deltaj.Paren;
import org.deltaj.deltaj.Plus;
import org.deltaj.deltaj.Product;
import org.deltaj.deltaj.ReturnStatement;
import org.deltaj.deltaj.Selection;
import org.deltaj.deltaj.Statement;
import org.deltaj.deltaj.StatementBlock;
import org.deltaj.deltaj.StringConstant;
import org.deltaj.deltaj.This;
import org.deltaj.deltaj.Type;
import org.deltaj.deltaj.TypeForDeclaration;
import org.deltaj.deltaj.TypeForMethod;
import org.deltaj.deltaj.TypedDeclaration;
import org.deltaj.deltaj.VariableAccess;
import org.deltaj.deltaj.VoidType;
import org.deltaj.generator.DeltaJConstraintsGenerator;
import org.deltaj.generator.DeltaJGeneratorExtensions;
import org.deltaj.util.ClassCollection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class DeltaJGenerator implements IGenerator {
  @Inject
  private DeltaJGeneratorExtensions generatorExtensions;
  
  @Inject
  private DeltaJConstraintsGenerator constraintsGenerator;
  
  public void doGenerate(final Resource resource, final IFileSystemAccess fsa) {
      TreeIterator<EObject> _allContents = resource.getAllContents();
      Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
      Iterable<Product> _filter = IterableExtensions.<Product>filter(_iterable, org.deltaj.deltaj.Product.class);
      for (final Product product : _filter) {
        this.compile(product, fsa);
      }
      this.constraintsGenerator.doGenerate(resource, fsa);
  }
  
  public void compile(final Product product, final IFileSystemAccess fsa) {
    ClassCollection _classesToGenerate = this.generatorExtensions.classesToGenerate(product);
    for (final org.deltaj.deltaj.Class clazz : _classesToGenerate) {
      this.compile(product, clazz, fsa);
    }
  }
  
  public void compile(final Product product, final org.deltaj.deltaj.Class clazz, final IFileSystemAccess fsa) {
    String _fileName = this.generatorExtensions.fileName(product, clazz);
    CharSequence _compile = this.compile(product, clazz);
    fsa.generateFile(_fileName, _compile);
  }
  
  public CharSequence compile(final Product product, final org.deltaj.deltaj.Class clazz) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package ");
    String _packageName = this.generatorExtensions.packageName(product);
    _builder.append(_packageName, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("public class ");
    String _name = clazz.getName();
    _builder.append(_name, "");
    _builder.append(" ");
    String _extendsClause = this.extendsClause(clazz);
    _builder.append(_extendsClause, "");
    _builder.append("{");
    _builder.newLineIfNotEmpty();
    {
      EList<Field> _fields = clazz.getFields();
      for(final Field f : _fields) {
        _builder.append("\t");
        CharSequence _fieldRep = this.fieldRep(f);
        _builder.append(_fieldRep, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t");
    _builder.newLine();
    {
      EList<Method> _methods = clazz.getMethods();
      for(final Method m : _methods) {
        _builder.append("\t");
        CharSequence _methodRep = this.methodRep(m);
        _builder.append(_methodRep, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public String extendsClause(final org.deltaj.deltaj.Class clazz) {
    String _xifexpression = null;
    String _extends = clazz.getExtends();
    boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_extends, null);
    if (_operator_notEquals) {
      String _extends_1 = clazz.getExtends();
      String _operator_plus = StringExtensions.operator_plus("extends ", _extends_1);
      String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, " ");
      _xifexpression = _operator_plus_1;
    } else {
      _xifexpression = "";
    }
    return _xifexpression;
  }
  
  public CharSequence fieldRep(final Field field) {
    StringConcatenation _builder = new StringConcatenation();
    TypeForDeclaration _type = field.getType();
    String _typeRep = this.typeRep(_type);
    _builder.append(_typeRep, "");
    _builder.append(" ");
    String _name = field.getName();
    _builder.append(_name, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected String _typeRep(final Type voidType) {
    return "";
  }
  
  protected String _typeRep(final VoidType voidType) {
    return "void";
  }
  
  protected String _typeRep(final BasicType basic) {
    String _basic = basic.getBasic();
    return _basic;
  }
  
  protected String _typeRep(final ClassType clazz) {
    String _classref = clazz.getClassref();
    return _classref;
  }
  
  public CharSequence methodRep(final Method method) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public ");
    TypeForMethod _returntype = method.getReturntype();
    String _typeRep = this.typeRep(_returntype);
    _builder.append(_typeRep, "");
    _builder.append(" ");
    String _name = method.getName();
    _builder.append(_name, "");
    _builder.append("(");
    String _parameterList = this.parameterList(method);
    _builder.append(_parameterList, "");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    {
      StatementBlock _body = method.getBody();
      boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_body, null);
      if (_operator_notEquals) {
        _builder.append("\t");
        StatementBlock _body_1 = method.getBody();
        CharSequence _bodyRep = this.bodyRep(_body_1);
        _builder.append(_bodyRep, "	");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    return _builder;
  }
  
  public String parameterList(final Method m) {
    EList<Parameter> _params = m.getParams();
    final Function1<Parameter,String> _function = new Function1<Parameter,String>() {
        public String apply(final Parameter p) {
          TypeForDeclaration _type = p.getType();
          String _typeRep = DeltaJGenerator.this.typeRep(_type);
          String _operator_plus = StringExtensions.operator_plus(_typeRep, " ");
          String _name = p.getName();
          String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, _name);
          return _operator_plus_1;
        }
      };
    List<String> _map = ListExtensions.<Parameter, String>map(_params, _function);
    String _join = IterableExtensions.join(_map, ", ");
    return _join;
  }
  
  public CharSequence bodyRep(final StatementBlock body) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<LocalVariableDeclaration> _localvariables = body.getLocalvariables();
      for(final LocalVariableDeclaration localvars : _localvariables) {
        TypeForDeclaration _type = localvars.getType();
        String _typeRep = this.typeRep(_type);
        _builder.append(_typeRep, "");
        _builder.append(" ");
        String _name = localvars.getName();
        _builder.append(_name, "");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      EList<Statement> _statements = body.getStatements();
      for(final Statement statement : _statements) {
        CharSequence _compileStatement = this.compileStatement(statement);
        _builder.append(_compileStatement, "");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  protected CharSequence _compileStatement(final ExpressionStatement expressionStatement) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _expression = expressionStatement.getExpression();
    CharSequence _compileExp = this.compileExp(_expression);
    _builder.append(_compileExp, "");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _compileStatement(final ReturnStatement returnStmt) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("return ");
    {
      Expression _expression = returnStmt.getExpression();
      boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_expression, null);
      if (_operator_notEquals) {
        Expression _expression_1 = returnStmt.getExpression();
        CharSequence _compileExp = this.compileExp(_expression_1);
        _builder.append(_compileExp, "");
      }
    }
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _compileStatement(final JavaVerbatim javaVerbatim) {
    String _extractJavaVerbatimCode = this.generatorExtensions.extractJavaVerbatimCode(javaVerbatim);
    return _extractJavaVerbatimCode;
  }
  
  protected CharSequence _compileStatement(final Assignment assignment) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _left = assignment.getLeft();
    CharSequence _compileExp = this.compileExp(_left);
    _builder.append(_compileExp, "");
    _builder.append(" = ");
    Expression _right = assignment.getRight();
    CharSequence _compileExp_1 = this.compileExp(_right);
    _builder.append(_compileExp_1, "");
    _builder.append(";");
    return _builder;
  }
  
  protected CharSequence _compileStatement(final IfStatement ifStatement) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("if (");
    Expression _expression = ifStatement.getExpression();
    CharSequence _compileExp = this.compileExp(_expression);
    _builder.append(_compileExp, "");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    StatementBlock _thenBlock = ifStatement.getThenBlock();
    CharSequence _bodyRep = this.bodyRep(_thenBlock);
    _builder.append(_bodyRep, "	");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    {
      StatementBlock _elseBlock = ifStatement.getElseBlock();
      boolean _operator_notEquals = ObjectExtensions.operator_notEquals(_elseBlock, null);
      if (_operator_notEquals) {
        _builder.append(" else {");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        StatementBlock _elseBlock_1 = ifStatement.getElseBlock();
        CharSequence _bodyRep_1 = this.bodyRep(_elseBlock_1);
        _builder.append(_bodyRep_1, "	");
        _builder.newLineIfNotEmpty();
        _builder.append("}");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _compileExp(final Expression expression) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("// ");
    EClass _eClass = expression.eClass();
    String _name = _eClass.getName();
    _builder.append(_name, "");
    _builder.append(" generation not yet implemented");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _compileExp(final Plus plus) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _left = plus.getLeft();
    CharSequence _compileExp = this.compileExp(_left);
    _builder.append(_compileExp, "");
    _builder.append(" + ");
    Expression _right = plus.getRight();
    CharSequence _compileExp_1 = this.compileExp(_right);
    _builder.append(_compileExp_1, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Minus minus) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _left = minus.getLeft();
    CharSequence _compileExp = this.compileExp(_left);
    _builder.append(_compileExp, "");
    _builder.append(" - ");
    Expression _right = minus.getRight();
    CharSequence _compileExp_1 = this.compileExp(_right);
    _builder.append(_compileExp_1, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final MultiOrDiv exp) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _left = exp.getLeft();
    CharSequence _compileExp = this.compileExp(_left);
    _builder.append(_compileExp, "");
    _builder.append(" ");
    String _op = exp.getOp();
    _builder.append(_op, "");
    _builder.append(" ");
    Expression _right = exp.getRight();
    CharSequence _compileExp_1 = this.compileExp(_right);
    _builder.append(_compileExp_1, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Comparison exp) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _left = exp.getLeft();
    CharSequence _compileExp = this.compileExp(_left);
    _builder.append(_compileExp, "");
    _builder.append(" ");
    String _op = exp.getOp();
    _builder.append(_op, "");
    _builder.append(" ");
    Expression _right = exp.getRight();
    CharSequence _compileExp_1 = this.compileExp(_right);
    _builder.append(_compileExp_1, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final AndOrExpression exp) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _left = exp.getLeft();
    CharSequence _compileExp = this.compileExp(_left);
    _builder.append(_compileExp, "");
    _builder.append(" ");
    String _op = exp.getOp();
    _builder.append(_op, "");
    _builder.append(" ");
    Expression _right = exp.getRight();
    CharSequence _compileExp_1 = this.compileExp(_right);
    _builder.append(_compileExp_1, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final BooleanNegation exp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("!");
    Expression _expression = exp.getExpression();
    CharSequence _compileExp = this.compileExp(_expression);
    _builder.append(_compileExp, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final ArithmeticSigned exp) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("-(");
    Expression _expression = exp.getExpression();
    CharSequence _compileExp = this.compileExp(_expression);
    _builder.append(_compileExp, "");
    _builder.append(")");
    return _builder;
  }
  
  protected CharSequence _compileExp(final This thiz) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("this");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Null nul) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("null");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Original original) {
    StringConcatenation _builder = new StringConcatenation();
    String _method = original.getMethod();
    _builder.append(_method, "");
    _builder.append("(");
    EList<Expression> _args = original.getArgs();
    String _compileArgs = this.compileArgs(_args);
    _builder.append(_compileArgs, "");
    _builder.append(")");
    return _builder;
  }
  
  protected CharSequence _compileExp(final IntConstant c) {
    StringConcatenation _builder = new StringConcatenation();
    int _constant = c.getConstant();
    _builder.append(_constant, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final StringConstant c) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\"");
    String _constant = c.getConstant();
    _builder.append(_constant, "");
    _builder.append("\"");
    return _builder;
  }
  
  protected CharSequence _compileExp(final BoolConstant c) {
    StringConcatenation _builder = new StringConcatenation();
    String _constant = c.getConstant();
    _builder.append(_constant, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final VariableAccess variable) {
    StringConcatenation _builder = new StringConcatenation();
    TypedDeclaration _variable = variable.getVariable();
    String _name = _variable.getName();
    _builder.append(_name, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final New n) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("new ");
    String _class_ = n.getClass_();
    _builder.append(_class_, "");
    _builder.append("()");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Cast cast) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    String _type = cast.getType();
    _builder.append(_type, "");
    _builder.append(") ");
    Expression _object = cast.getObject();
    CharSequence _compileExp = this.compileExp(_object);
    _builder.append(_compileExp, "");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Paren paren) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    Expression _expression = paren.getExpression();
    CharSequence _compileExp = this.compileExp(_expression);
    _builder.append(_compileExp, "");
    _builder.append(")");
    return _builder;
  }
  
  protected CharSequence _compileExp(final Selection sel) {
    StringConcatenation _builder = new StringConcatenation();
    Expression _receiver = sel.getReceiver();
    CharSequence _compileExp = this.compileExp(_receiver);
    _builder.append(_compileExp, "");
    _builder.append(".");
    Message _message = sel.getMessage();
    CharSequence _compileMessage = this.compileMessage(_message);
    _builder.append(_compileMessage, "");
    return _builder;
  }
  
  protected CharSequence _compileMessage(final Message message) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(" ");
    return _builder;
  }
  
  protected CharSequence _compileMessage(final FieldSelection field) {
    StringConcatenation _builder = new StringConcatenation();
    String _field = field.getField();
    _builder.append(_field, "");
    return _builder;
  }
  
  protected CharSequence _compileMessage(final MethodCall methodCall) {
    StringConcatenation _builder = new StringConcatenation();
    String _method = methodCall.getMethod();
    _builder.append(_method, "");
    _builder.append("(");
    EList<Expression> _args = methodCall.getArgs();
    String _compileArgs = this.compileArgs(_args);
    _builder.append(_compileArgs, "");
    _builder.append(")");
    return _builder;
  }
  
  public String compileArgs(final List<Expression> args) {
    final Function1<Expression,CharSequence> _function = new Function1<Expression,CharSequence>() {
        public CharSequence apply(final Expression arg) {
          CharSequence _compileExp = DeltaJGenerator.this.compileExp(arg);
          return _compileExp;
        }
      };
    List<CharSequence> _map = ListExtensions.<Expression, CharSequence>map(args, _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(", ");
    String _join = IterableExtensions.join(_map, _builder);
    return _join;
  }
  
  public String typeRep(final Type basic) {
    if (basic instanceof BasicType) {
      return _typeRep((BasicType)basic);
    } else if (basic instanceof ClassType) {
      return _typeRep((ClassType)basic);
    } else if (basic instanceof VoidType) {
      return _typeRep((VoidType)basic);
    } else if (basic != null) {
      return _typeRep(basic);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(basic).toString());
    }
  }
  
  public CharSequence compileStatement(final Statement assignment) {
    if (assignment instanceof Assignment) {
      return _compileStatement((Assignment)assignment);
    } else if (assignment instanceof ExpressionStatement) {
      return _compileStatement((ExpressionStatement)assignment);
    } else if (assignment instanceof IfStatement) {
      return _compileStatement((IfStatement)assignment);
    } else if (assignment instanceof JavaVerbatim) {
      return _compileStatement((JavaVerbatim)assignment);
    } else if (assignment instanceof ReturnStatement) {
      return _compileStatement((ReturnStatement)assignment);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(assignment).toString());
    }
  }
  
  public CharSequence compileExp(final Expression c) {
    if (c instanceof BoolConstant) {
      return _compileExp((BoolConstant)c);
    } else if (c instanceof IntConstant) {
      return _compileExp((IntConstant)c);
    } else if (c instanceof StringConstant) {
      return _compileExp((StringConstant)c);
    } else if (c instanceof AndOrExpression) {
      return _compileExp((AndOrExpression)c);
    } else if (c instanceof ArithmeticSigned) {
      return _compileExp((ArithmeticSigned)c);
    } else if (c instanceof BooleanNegation) {
      return _compileExp((BooleanNegation)c);
    } else if (c instanceof Cast) {
      return _compileExp((Cast)c);
    } else if (c instanceof Comparison) {
      return _compileExp((Comparison)c);
    } else if (c instanceof Minus) {
      return _compileExp((Minus)c);
    } else if (c instanceof MultiOrDiv) {
      return _compileExp((MultiOrDiv)c);
    } else if (c instanceof New) {
      return _compileExp((New)c);
    } else if (c instanceof Null) {
      return _compileExp((Null)c);
    } else if (c instanceof Original) {
      return _compileExp((Original)c);
    } else if (c instanceof Paren) {
      return _compileExp((Paren)c);
    } else if (c instanceof Plus) {
      return _compileExp((Plus)c);
    } else if (c instanceof Selection) {
      return _compileExp((Selection)c);
    } else if (c instanceof This) {
      return _compileExp((This)c);
    } else if (c instanceof VariableAccess) {
      return _compileExp((VariableAccess)c);
    } else if (c != null) {
      return _compileExp(c);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(c).toString());
    }
  }
  
  public CharSequence compileMessage(final Message field) {
    if (field instanceof FieldSelection) {
      return _compileMessage((FieldSelection)field);
    } else if (field instanceof MethodCall) {
      return _compileMessage((MethodCall)field);
    } else if (field != null) {
      return _compileMessage(field);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(field).toString());
    }
  }
}
