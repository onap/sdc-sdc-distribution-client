from docs_conf.conf import *

branch = 'latest'
master_doc = 'index'

intersphinx_mapping = {}

linkcheck_ignore = [
    'http://localhost',
]


html_last_updated_fmt = '%d-%b-%y %H:%M'

def setup(app):
    app.add_css_file("css/ribbon.css")
