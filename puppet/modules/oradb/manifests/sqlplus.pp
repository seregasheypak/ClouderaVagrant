define oradb::sqlplus(
  $command = $name,  
)

{
  exec{"$command":
    path => '/oracle/product/11.2/db/bin/',
    environment => ['ORACLE_BASE=/oracle', 'ORACLE_HOME=/oracle/product/11.2/db', 'ORACLE_UNQNAME=TEST', 'ORACLE_SID=test', 'NLS_LANG=AMERICAN_AMERICA.CL8ISO8859P5', "NLS_DATE_FORMAT='DD.MM.YYYY'"],
    command => "sqlplus -s \"/as sysdba\" <<< \"$command\"",
    user => 'oracle',
  }
}
