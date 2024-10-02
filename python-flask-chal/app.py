from flask import Flask, render_template, request
import jinja2

app = Flask(__name__)

@app.route('/', methods=['GET'])
def calculator():
    expression = request.args.get('expression', '')
    result = ''
    if expression:
        try:
            # This is where the SSTI vulnerability is introduced
            template = jinja2.Template("{{ " + expression + " }}")
            result = template.render()
        except Exception as e:
            result = "Error: " + str(e)
    return render_template('calculator.html', expression=expression, result=result)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
