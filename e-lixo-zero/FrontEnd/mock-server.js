const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 8087;

app.use(cors());
app.use(express.json());

const dbPath = path.join(__dirname, 'db.json');
const raw = fs.readFileSync(dbPath, 'utf8');
const db = JSON.parse(raw);

function findById(list, id) {
  return list.find((item) => item.id == id);
}

function nextId(list) {
  if (!list || list.length === 0) return 1;
  const max = Math.max(...list.map((item) => Number(item.id) || 0));
  return max + 1;
}

function today() {
  const today = new Date();
  return today.toISOString().split('T')[0];
}

// USERS

app.post('/api/users/login', (req, res) => {
  const { email, password } = req.body;
  const user = db.users.find((u) => u.email === email && u.password === password);

  if (!user) {
    return res.status(401).json({ message: 'Invalid email or password' });
  }

  res.json({
    id: user.id,
    fullName: user.fullName,
    email: user.email,
    cpf: user.cpf || '',
    phone: user.phone || '',
    street: user.street || '',
    number: user.number || '',
    neighborhood: user.neighborhood || '',
    city: user.city || '',
    state: user.state || 'MG',
    userType: user.userType || 'CITIZEN',
    token: 'mock-token',
  });
});

app.get('/api/users', (req, res) => {
  res.json(db.users);
});

app.get('/api/users/:id', (req, res) => {
  const user = findById(db.users, req.params.id);
  if (!user) return res.status(404).json({ message: 'User not found' });
  res.json(user);
});

app.post('/api/users', (req, res) => {
  const created = { ...req.body, id: nextId(db.users) };
  db.users.push(created);
  res.status(201).json(created);
});

app.put('/api/users/:id', (req, res) => {
  const index = db.users.findIndex((u) => u.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'User not found' });
  db.users[index] = { ...req.body, id: db.users[index].id };
  res.json(db.users[index]);
});

app.delete('/api/users/:id', (req, res) => {
  const index = db.users.findIndex((u) => u.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'User not found' });
  db.users.splice(index, 1);
  res.status(204).send();
});

// COLLECTION POINTS

app.get('/api/collection-points', (req, res) => {
  res.json(db['collection-points']);
});

app.get('/api/collection-points/city/:city', (req, res) => {
  res.json(db['collection-points']);
});

app.get('/api/collection-points/:id', (req, res) => {
  const point = findById(db['collection-points'], req.params.id);
  if (!point) return res.status(404).json({ message: 'Collection point not found' });
  res.json(point);
});

app.post('/api/collection-points', (req, res) => {
  const created = { ...req.body, id: nextId(db['collection-points']) };
  db['collection-points'].push(created);
  res.status(201).json(created);
});

app.put('/api/collection-points/:id', (req, res) => {
  const list = db['collection-points'];
  const index = list.findIndex((p) => p.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Collection point not found' });
  list[index] = { ...req.body, id: list[index].id };
  res.json(list[index]);
});

app.delete('/api/collection-points/:id', (req, res) => {
  const list = db['collection-points'];
  const index = list.findIndex((p) => p.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Collection point not found' });
  list.splice(index, 1);
  res.status(204).send();
});

// WASTE TYPES

app.get('/api/waste-types', (req, res) => {
  res.json(db.wasteTypes);
});

app.get('/api/waste-types/category/:category', (req, res) => {
  const filtered = db.wasteTypes.filter((r) => r.category === req.params.category);
  res.json(filtered);
});

app.get('/api/waste-types/:id', (req, res) => {
  const waste = findById(db.wasteTypes, req.params.id);
  if (!waste) return res.status(404).json({ message: 'Waste type not found' });
  res.json(waste);
});

app.post('/api/waste-types', (req, res) => {
  const created = { ...req.body, id: nextId(db.wasteTypes) };
  db.wasteTypes.push(created);
  res.status(201).json(created);
});

app.put('/api/waste-types/:id', (req, res) => {
  const index = db.wasteTypes.findIndex((r) => r.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Waste type not found' });
  db.wasteTypes[index] = { ...req.body, id: db.wasteTypes[index].id };
  res.json(db.wasteTypes[index]);
});

app.delete('/api/waste-types/:id', (req, res) => {
  const index = db.wasteTypes.findIndex((r) => r.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Waste type not found' });
  db.wasteTypes.splice(index, 1);
  res.status(204).send();
});

// PICKUPS

app.get('/api/pickups', (req, res) => {
  res.json(db.pickups);
});

app.get('/api/pickups/:id', (req, res) => {
  const pickup = findById(db.pickups, req.params.id);
  if (!pickup) return res.status(404).json({ message: 'Pickup not found' });
  res.json(pickup);
});

app.post('/api/pickups', (req, res) => {
  const created = { ...req.body, id: String(nextId(db.pickups)) };
  db.pickups.push(created);

  const wasteName = req.body.waste || 'Resíduo';
  const formattedDate = req.body.date ? req.body.date.split('-').reverse().join('/') : today();
  const period = req.body.period || 'Morning';

  db.notifications.push({
    id: nextId(db.notifications),
    userId: req.body.userId || 1,
    title: 'Pickup confirmed',
    message: `Sua pickup de ${wasteName} foi scheduled para ${formattedDate} no período da ${period.toLowerCase()}.`,
    notificationType: 'INFO',
    read: false,
    sentAt: today(),
    date: today(),
  });

  res.status(201).json(created);
});

app.put('/api/pickups/:id/status', (req, res) => {
  const index = db.pickups.findIndex((c) => c.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Pickup not found' });
  db.pickups[index].status = req.body.status;
  res.json(db.pickups[index]);
});

app.put('/api/pickups/:id', (req, res) => {
  const index = db.pickups.findIndex((c) => c.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Pickup not found' });
  db.pickups[index] = { ...req.body, id: db.pickups[index].id };
  res.json(db.pickups[index]);
});

app.delete('/api/pickups/:id', (req, res) => {
  const index = db.pickups.findIndex((c) => c.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Pickup not found' });
  db.pickups.splice(index, 1);
  res.status(204).send();
});

// NOTIFICATIONS

app.get('/api/notifications', (req, res) => {
  res.json(db.notifications);
});

app.get('/api/notifications/user/:userId/unread', (req, res) => {
  res.json(db.notifications.filter((n) => !n.read));
});

app.get('/api/notifications/user/:userId', (req, res) => {
  res.json(db.notifications);
});

app.get('/api/notifications/:id', (req, res) => {
  const notification = findById(db.notifications, req.params.id);
  if (!notification) return res.status(404).json({ message: 'Notification not found' });
  res.json(notification);
});

app.post('/api/notifications', (req, res) => {
  const created = { ...req.body, id: nextId(db.notifications) };
  db.notifications.push(created);
  res.status(201).json(created);
});

app.put('/api/notifications/:id/mark-read', (req, res) => {
  const index = db.notifications.findIndex((n) => n.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Notification not found' });
  db.notifications[index].read = true;
  res.json(db.notifications[index]);
});

app.put('/api/notifications/:id', (req, res) => {
  const index = db.notifications.findIndex((n) => n.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Notification not found' });
  db.notifications[index] = { ...req.body, id: db.notifications[index].id };
  res.json(db.notifications[index]);
});

app.delete('/api/notifications/:id', (req, res) => {
  const index = db.notifications.findIndex((n) => n.id == req.params.id);
  if (index < 0) return res.status(404).json({ message: 'Notification not found' });
  db.notifications.splice(index, 1);
  res.status(204).send();
});

app.listen(PORT, () => {
  console.log(`Mock server running at http://localhost:${PORT}`);
});
