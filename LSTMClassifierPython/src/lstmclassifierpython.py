from pymongo import MongoClient

client = MongoClient('localhost', 27017)

db = client.nlp

reviews = db.foodsentiment.find()

foodLabel = []
priceLabel = []
serviceLabel = []
ambienceLabel = []
textTrain = []

for review in reviews:
	foodLabel.append(review.get('foodPolarityInteger'))
	priceLabel.append(review.get('pricePolarityInteger'))
	serviceLabel.append(review.get('servicePolarityInteger'))
	ambienceLabel.append(review.get('ambiencePolarityInteger'))
	textTrain.append(review.get('translatedText'))


from keras.preprocessing.text import one_hot
from keras.preprocessing.sequence import pad_sequences
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import Flatten
from keras.layers import LSTM
from keras.layers.embeddings import Embedding

# integer encode the documents
vocab_size = 100
encoded_docs = [one_hot(d, vocab_size) for d in textTrain]

max_length = 200
padded_docs = pad_sequences(encoded_docs, maxlen=max_length, padding='post')

model = Sequential()
model.add(Embedding(vocab_size, 8, input_length=max_length))
model.add(Flatten())
model.add(Dense(32,activation='relu',kernel_initializer='uniform'))
model.add(LSTM(100))
model.add(Dense(1, activation='sigmoid'))
model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['acc'])
print(model.summary())

model.fit(padded_docs, foodLabel, epochs=1, verbose=1, batch_size=100)
loss, accuracy = model.evaluate(padded_docs, foodLabel, verbose=1)
print('Accuracy: %f' % (accuracy*100),"\n")
