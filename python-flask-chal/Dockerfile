FROM python:3.9
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .
COPY templates ./templates

# Create the /secret directory and add the flag
RUN mkdir -p /secret && echo "Flag{J1nj@_sSTi_Att@ck}" > /secret/flag.txt

CMD ["python", "app.py"]