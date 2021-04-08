

import groovy.sql.Sql

class Main {
    static void main(String[] args) {
        def path = System.getProperty("user.home") + '/clob-files/'
        new File(path).mkdirs()

        def sql = Sql.newInstance("jdbc:oracle:thin:@pri4acq.online-acq.local:1522:PRI4ACQ", "MIR", "MIR", "oracle.jdbc.OracleDriver")

        def rows = sql.rows("select * from mir.POS_REGISTRY_FILES prf where prf.PROGRAM_TYPE = 'KFC' and prf.FDAY between to_date('2020-11-01', 'yyyy-mm-dd') and to_date('2020-11-02', 'yyyy-mm-dd')")

        rows.each {
            def filename = it.get('FILENAME') as String
            def clob = it.get('FILEDATA') as java.sql.Clob

            def file = new File(path + filename)
            file.createNewFile()
            file.write(clob.characterStream.text)
        }
    }
}
